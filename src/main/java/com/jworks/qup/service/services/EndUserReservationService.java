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
import com.jworks.qup.service.enums.GetReservationAction;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.jworks.app.commons.utils.AppUtil.validateDatePair;
import static com.jworks.app.commons.utils.AppUtil.validateTransactionDate;
import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_QUEUE_RESERVATION;
import static com.jworks.qup.service.enums.GetReservationAction.BY_QUEUE_OWNER;
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
    private final QueueIdSequenceGenerator queueIdSequenceGenerator;

    public EndUserReservationService(EndUserReservationRepository endUserReservationRepository, EndUserService endUserService,
                                     EndUserQueueService endUserQueueService, QueueIdSequenceGenerator queueIdSequenceGenerator) {
        super(endUserReservationRepository);
        this.endUserReservationRepository = endUserReservationRepository;
        this.endUserService = endUserService;
        this.endUserQueueService = endUserQueueService;
        this.queueIdSequenceGenerator = queueIdSequenceGenerator;
    }

    public String createReservation(CreateReservationDto createReservationDto, String userReference) throws NotFoundRestApiException, SystemServiceException, UnProcessableOperationException {

        Long queueId = createReservationDto.getQueueId();

        String reservationCode = ReferenceGenerator.generateRef(INTENT_QUEUE_RESERVATION);

        if(endUserReservationRepository.existsByEndUserUserReferenceAndEndUserQueueIdAndReservationStatusNot(userReference, queueId, ReservationStatus.COMPLETED)){
            throw new UnProcessableOperationException("You already have an active reservation");
        }

        EndUser userOwnerOfReservation = endUserService.getUserByUserReference(userReference);

        EndUserQueue queueBeingReserved = endUserQueueService.getQueueById(queueId);

        Long joinId = queueIdSequenceGenerator.generateNextJoinId(queueId);
        EndUserReservation endUserReservation = EndUserReservation.builder()
                .reservationCode(reservationCode)
                .reservationStatus(ReservationStatus.WAITING)
                .endUser(userOwnerOfReservation)
                .endUserQueue(queueBeingReserved)
                .joinId(joinId)
                .build();

        save(endUserReservation);

        return reservationCode;
    }


    public PageOutput<EndUserReservationDto> getReservationByQueue(ClientSearchReservationDto clientSearchReservationDto,String userReference, long queueId, PageRequest pageRequest) throws NotFoundRestApiException, BadRequestException {

        EndUserQueue endUserQueue = endUserQueueService.getQueueById(queueId);

        if(!userReference.equalsIgnoreCase(endUserQueue.getEndUser().getUserReference())) throw new UnauthorizedUserException("Cannot access reservations belonging to another user. Confirm you are logged in as the right user.");

        clientSearchReservationDto.setQueueCode(endUserQueue.getQueueCode());

        return getReservations(clientSearchReservationDto, userReference, BY_QUEUE_OWNER, pageRequest);

    }

    public PageOutput<EndUserReservationDto> getReservations(ClientSearchReservationDto clientSearchReservationDto, String userReference, GetReservationAction getReservationAction, PageRequest pageRequest) throws BadRequestException {

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
        Page<EndUserReservationDto> endUserReservations;

        switch (getReservationAction){

            case BY_USER_OWNER:
                endUserReservations = endUserReservationRepository.getReservationsBelongingToUser(
                        clientSearchReservationDto.getReservationCode(),
                        validatedCreatedOnStartDate,validatedCreatedOnEndDate,
                        clientSearchReservationDto.getQueueCode(),reservationStatus,
                        userReference,pageRequest
                ).map(EndUserReservationDto::new);
                break;

            case BY_QUEUE_OWNER:
                endUserReservations = endUserReservationRepository.getReservationsByQueueOwner(
                        clientSearchReservationDto.getReservationCode(),
                        validatedCreatedOnStartDate,validatedCreatedOnEndDate,
                        clientSearchReservationDto.getQueueCode(),reservationStatus,
                        userReference,pageRequest
                ).map(EndUserReservationDto::new);
                break;

             default:
                 throw new IllegalArgumentException(String.format("Unrecognized value: %s, for getReservationAction parameter", getReservationAction));

        }

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
