package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.providers.DtoProvider;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;

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
        if (endUserReservation.getEndUserQueue() != null)
            endUserQueueProvider.save(endUserReservation.getEndUserQueue());
    }

    @Override
    public EndUserReservationRepository getRepository() {
        return endUserReservationRepository;
    }
}
