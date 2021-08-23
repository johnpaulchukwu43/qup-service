package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.models.EndUserReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserReservationRepository extends BaseRepository<EndUserReservation,Long> {

    Optional<EndUserReservation> findByReservationCode(String reservationCode);

    Boolean existsByEndUserUserReferenceAndEndUserQueueIdAndReservationStatusNot(String userReference, Long queueId, ReservationStatus reservationStatus);

    @Query(
            "SELECT new com.jworks.qup.service.models.EndUserReservationDto(eur) FROM EndUserReservation eur " +
                    "WHERE ((:reservationStatus IS NULL) OR (eur.reservationStatus = :reservationStatus))" +
                    "AND ((:queueCode IS NULL) OR (eur.endUserQueue.queueCode = :queueCode))" +
                    "AND ((:userReference IS NULL) OR (eur.endUser.userReference = :userReference))" +
                    "AND ((:reservationCode IS NULL) OR (eur.reservationCode = :reservationCode))"+
                    "AND ((:createdOnStartDate IS NULL AND :createdOnEndDate IS NULL)" +
                    "OR (eur.createdAt >= :createdOnStartDate AND eur.createdAt <= :createdOnEndDate))"

    )
    Page<EndUserReservationDto> getReservationsFiltered(
            @Param("reservationCode") String reservationCode,
            @Param("createdOnStartDate") Timestamp createdOnStartDate,
            @Param("createdOnEndDate") Timestamp createdOnEndDate,
            @Param("queueCode") String queueCode,
            @Param("reservationStatus") ReservationStatus reservationStatus,
            @Param("userReference") String userReference,
            Pageable pageable
    );
}
