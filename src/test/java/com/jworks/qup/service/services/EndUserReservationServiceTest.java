package com.jworks.qup.service.services;

import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.providers.impl.EndUserReservationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link EndUserReservationService}.
 *
 * @author bodmas
 * @since Oct 5, 2021.
 */
public class EndUserReservationServiceTest extends AbstractServiceTest {

    private EndUserReservationService endUserReservationService;

    @Autowired
    private EndUserReservationProvider endUserReservationProvider;

    @MockBean
    private EndUserService endUserService;
    @MockBean
    private EndUserQueueService endUserQueueService;

    @BeforeEach
    public void setUp() {
        endUserReservationService = new EndUserReservationService(
                endUserReservationProvider.getRepository(), endUserService, endUserQueueService
        );
    }

    @Test
    public void createReservationShouldSucceed() throws Exception {
        EndUserReservation endUserReservation = endUserReservationProvider.provide();
        endUserReservationProvider.preSave(endUserReservation);

        final EndUser endUser = endUserReservation.getEndUser();
        final EndUserQueue endUserQueue = endUserReservation.getEndUserQueue();

        willReturn(endUser).given(endUserService).getUserByUserReference(endUser.getUserReference());
        willReturn(endUserQueue).given(endUserQueueService).getQueueById(endUserQueue.getId());

        String reservationCode = endUserReservationService.createReservation(
                CreateReservationDto.builder().queueId(endUserQueue.getId()).build(), endUser.getUserReference()
        );
        assertThat(reservationCode).isNotNull();
    }
}
