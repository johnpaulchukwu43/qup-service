package com.jworks.qup.service.controllers;

import com.jworks.qup.service.providers.impl.EndUserReservationProvider;
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
    private EndUserReservationProvider endUserReservationProvider;

    public EndUserReservationControllerTest() {
        super(EndUserReservationController.class);
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createReservationShouldSucceed() throws Exception {
        asAdmin(post(endUserReservationProvider.provideDto(), endpoint()))
                .andDo(print())
                .andExpect(successStatus())
                .andExpect(data("reservationCode").isNotEmpty());
    }
}
