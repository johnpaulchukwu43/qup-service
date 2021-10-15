package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.providers.DtoProvider;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import lombok.AllArgsConstructor;
import com.jworks.qup.service.providers.EntityProvider;
import org.springframework.stereotype.Component;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Component
@AllArgsConstructor
public class EndUserReservationProvider implements EntityProvider<EndUserReservation>, DtoProvider<CreateReservationDto> {

    private final EndUserReservationRepository endUserReservationRepository;
    private final EndUserProvider endUserProvider;
    private final EndUserQueueProvider endUserQueueProvider;

    @Override
    public EndUserReservation provide() {
        EndUserReservation endUserReservation = EndUserReservation.builder()
                .endUser(endUserProvider.provide())
                .endUserQueue(endUserQueueProvider.provide())
                .reservationCode(faker.code().ean8())
                .reservationStatus(ReservationStatus.WAITING)
                .build();
        return endUserReservation;
    }

    @Override
    public CreateReservationDto provideDto() {
        return CreateReservationDto.builder().queueId(endUserQueueProvider.provideAndSave().getId()).build();
    }

    @Override
    public void preSave(EndUserReservation endUserReservation) {
        if (endUserReservation.getEndUser() != null)
            endUserProvider.save(endUserReservation.getEndUser());
        if (endUserReservation.getEndUserQueue() != null) {
            EndUserQueue endUserQueue = endUserQueueProvider.save(endUserReservation.getEndUserQueue());
            if (endUserReservation.getJoinId() == null)
                endUserReservation.setJoinId(endUserQueueProvider.getNextJoinId(endUserQueue));
        }
    }

    @Override
    public boolean postDelete(EndUserReservation endUserReservation) {
        boolean isWithoutErrors = true;
        if (endUserReservation.getEndUser() != null)
            isWithoutErrors &= endUserProvider.delete(endUserReservation.getEndUser());
        if (endUserReservation.getEndUserQueue() != null)
            isWithoutErrors &= endUserQueueProvider.delete(endUserReservation.getEndUserQueue());
        return isWithoutErrors;
    }

    @Override
    public EndUserReservationRepository getRepository() {
        return endUserReservationRepository;
    }
}
