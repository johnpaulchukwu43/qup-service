package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.models.EndUserReservationDto;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_QUEUE_RESERVATION;

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

    @Override
    public EndUserReservationDto convertEntityToDto(EndUserReservation entity) {
        return new EndUserReservationDto(entity);
    }
}
