package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import org.springframework.stereotype.Repository;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserReservationRepository extends BaseRepository<EndUserReservation,Long> {

    Boolean existsByEndUserUserReferenceAndEndUserQueueIdAndReservationStatusNot(String userReference, Long queueId, ReservationStatus reservationStatus);
}
