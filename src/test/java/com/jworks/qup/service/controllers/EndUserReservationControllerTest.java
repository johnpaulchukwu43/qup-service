package com.jworks.qup.service.controllers;

import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.models.EndUserQueueDto;
import com.jworks.qup.service.providers.impl.EndUserQueueProvider;
import com.jworks.qup.service.services.EndUserQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link EndUserReservationController}.
 *
 * @author bodmas
 * @since Oct 5, 2021.
 */
public class EndUserReservationControllerTest extends AbstractResourceTest {

    @Autowired
    private EndUserQueueService endUserQueueService;
    @Autowired
    private EndUserQueueProvider endUserQueueProvider;

    public EndUserReservationControllerTest() {
        super(EndUserReservationController.class);
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createReservationShouldSucceed() throws Exception {
        EndUserQueueDto endUserQueueDto = endUserQueueService.createQueue(endUserQueueProvider.provideDto(), adminUsername);
        asAdmin(post(CreateReservationDto.builder().queueId(endUserQueueDto.getId()).build(), endpoint()))
                .andDo(print())
                .andExpect(successStatus())
                .andExpect(data("reservationCode").isNotEmpty());
    }
}
