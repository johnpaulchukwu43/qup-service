package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserReservationRepository extends BaseRepository<EndUserReservation> {

    Optional<EndUserReservation> findByReservationCode(String reservationCode);
    boolean existsByReservationCode(String reservationCode);

    Boolean existsByEndUserUserReferenceAndEndUserQueueIdAndReservationStatusNot(String userReference, Long queueId, ReservationStatus reservationStatus);

    @Query(
            "SELECT eur FROM EndUserReservation eur " +
                    "WHERE (eur.endUser.userReference = :userReference)" +
                    "AND ((:reservationStatus IS NULL) OR (eur.reservationStatus = :reservationStatus))" +
                    "AND ((:queueCode IS NULL) OR (eur.endUserQueue.queueCode = :queueCode))" +
                    "AND ((:reservationCode IS NULL) OR (eur.reservationCode = :reservationCode))"+
                    "AND ((:createdOnStartDate IS NULL AND :createdOnEndDate IS NULL)" +
                    "OR (eur.createdAt >= :createdOnStartDate AND eur.createdAt <= :createdOnEndDate))"

    )
    Page<EndUserReservation> getReservationsBelongingToUser(
            @Param("reservationCode") String reservationCode,
            @Param("createdOnStartDate") Timestamp createdOnStartDate,
            @Param("createdOnEndDate") Timestamp createdOnEndDate,
            @Param("queueCode") String queueCode,
            @Param("reservationStatus") ReservationStatus reservationStatus,
            @Param("userReference") String userReference,
            Pageable pageable
    );

    @Query(
            "SELECT eur FROM EndUserReservation eur " +
                    "WHERE (eur.endUserQueue.endUser.userReference = :userReference)" +
                    "AND ((:reservationStatus IS NULL) OR (eur.reservationStatus = :reservationStatus))" +
                    "AND ((:queueCode IS NULL) OR (eur.endUserQueue.queueCode = :queueCode))" +
                    "AND ((:reservationCode IS NULL) OR (eur.reservationCode = :reservationCode))"+
                    "AND ((:createdOnStartDate IS NULL AND :createdOnEndDate IS NULL)" +
                    "OR (eur.createdAt >= :createdOnStartDate AND eur.createdAt <= :createdOnEndDate))"

    )
    Page<EndUserReservation> getReservationsByQueueOwner(
            @Param("reservationCode") String reservationCode,
            @Param("createdOnStartDate") Timestamp createdOnStartDate,
            @Param("createdOnEndDate") Timestamp createdOnEndDate,
            @Param("queueCode") String queueCode,
            @Param("reservationStatus") ReservationStatus reservationStatus,
            @Param("userReference") String userReference,
            Pageable pageable
    );
}
