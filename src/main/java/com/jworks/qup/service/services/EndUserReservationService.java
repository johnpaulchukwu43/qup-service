package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.PageOutput;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.jworks.app.commons.utils.AppUtil.validateDatePair;
import static com.jworks.app.commons.utils.AppUtil.validateTransactionDate;
import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_QUEUE_RESERVATION;
import static com.jworks.qup.service.enums.ReservationStatus.toReservationStatus;

/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
public class EndUserReservationService extends ServiceBluePrintImpl<EndUserReservation, EndUserReservationDto> {

    private final EndUserReservationRepository endUserReservationRepository;
    private final EndUserService endUserService;
    private final EndUserQueueService endUserQueueService;

    public EndUserReservationService(EndUserReservationRepository endUserReservationRepository, EndUserService endUserService, EndUserQueueService endUserQueueService) {
        super(endUserReservationRepository);
        this.endUserReservationRepository = endUserReservationRepository;
        this.endUserService = endUserService;
        this.endUserQueueService = endUserQueueService;
    }

    public String createReservation(CreateReservationDto createReservationDto, String userReference) throws NotFoundRestApiException, SystemServiceException, UnProcessableOperationException {

        Long queueId = createReservationDto.getQueueId();

        String reservationCode = ReferenceGenerator.generateRef(INTENT_QUEUE_RESERVATION);

        if(endUserReservationRepository.existsByEndUserUserReferenceAndEndUserQueueIdAndReservationStatusNot(userReference, queueId, ReservationStatus.COMPLETED)){
            throw new UnProcessableOperationException("You already have an active reservation");
        }

        EndUser userOwnerOfReservation = endUserService.getUserByUserReference(userReference);

        EndUserQueue queueBeingReserved = endUserQueueService.getQueueById(queueId);

        EndUserReservation endUserReservation = EndUserReservation.builder()
                .reservationCode(reservationCode)
                .reservationStatus(ReservationStatus.WAITING)
                .endUser(userOwnerOfReservation)
                .endUserQueue(queueBeingReserved)
                .build();

        save(endUserReservation);

        return reservationCode;
    }

    public PageOutput<EndUserReservationDto> getReservationsBelongingToUser(ClientSearchReservationDto clientSearchReservationDto, String userReference, PageRequest pageRequest) throws BadRequestException {

        ReservationStatus reservationStatus = null;

        Timestamp validatedCreatedOnStartDate = null;
        Timestamp validatedCreatedOnEndDate = null;

        if(StringUtils.isNotBlank(clientSearchReservationDto.getReservationStatus())){
            reservationStatus = toReservationStatus(clientSearchReservationDto.getReservationStatus());
        }

        LocalDateTime createdOnStartDate = validateTransactionDate(clientSearchReservationDto.getCreatedOnStartDate(), true);
        LocalDateTime createdOnEndDate = validateTransactionDate(clientSearchReservationDto.getCreatedOnEndDate(), false);

        validateDatePair(createdOnStartDate, createdOnEndDate);

        if (createdOnStartDate != null) validatedCreatedOnStartDate = Timestamp.valueOf(createdOnStartDate);
        if (createdOnEndDate != null) validatedCreatedOnEndDate = Timestamp.valueOf(createdOnEndDate);


        Page<EndUserReservationDto> endUserReservations = endUserReservationRepository.getReservationsFiltered(
                clientSearchReservationDto.getReservationCode(),
                validatedCreatedOnStartDate,validatedCreatedOnEndDate,
                clientSearchReservationDto.getQueueCode(),reservationStatus,
                userReference,pageRequest
        );

        return  PageOutput.fromPage(endUserReservations);

    }

    public void updateReservationStatus(EndUserReservationStatusDto endUserReservationStatusDto, String reservationCode) throws NotFoundRestApiException, SystemServiceException {

        EndUserReservation endUserReservation = getReservationByCode(reservationCode);

        ReservationStatus reservationStatus = toReservationStatus(endUserReservationStatusDto.getReservationStatus());

        endUserReservation.setReservationStatus(reservationStatus);

        save(endUserReservation);
    }

    private EndUserReservation getReservationByCode(String reservationCode) throws NotFoundRestApiException {
        return endUserReservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("Reservation with code: %s not found.", reservationCode)));
    }

    @Override
    public EndUserReservationDto convertEntityToDto(EndUserReservation entity) {
        return new EndUserReservationDto(entity);
    }
}
