package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserQueueRepository extends BaseRepository<EndUserQueue> {

    @Query(
            "SELECT euq FROM EndUserQueue euq " +
                    "WHERE ((:queueStatus IS NULL) OR (euq.queueStatus = :queueStatus))" +
                    " AND ((:queueCode IS NULL) OR (euq.queueCode = :queueCode))" +
                    " AND ((:queuePurpose IS NULL) OR (euq.queuePurpose = :queuePurpose))"+
                    " AND (euq.endUser.userReference = :userReference)" +
                    "AND ((:createdOnStartDate IS NULL AND :createdOnEndDate IS NULL)" +
                    "OR (euq.createdAt >= :createdOnStartDate AND euq.createdAt <= :createdOnEndDate))" +
                    "AND ((:expiryStartDate IS NULL AND :expiryEndDate IS NULL)" +
                    "OR (euq.expirationDate >= :expiryStartDate AND euq.expirationDate <= :expiryEndDate))"


    )
    Page<EndUserQueue> getAllQueuesByUserReferenceFilteredBy(
            @Param("queueStatus") QueueStatus queueStatus,
            @Param("queueCode") String queueCode,
            @Param("queuePurpose") QueuePurpose queuePurpose,
            @Param("userReference") String userReference,
            @Param("createdOnStartDate") Timestamp createdOnStartDate,
            @Param("createdOnEndDate") Timestamp createdOnEndDate,
            @Param("expiryStartDate") LocalDateTime expiryStartDate,
            @Param("expiryEndDate") LocalDateTime expiryEndDate,
            Pageable pageable
    );

    @Query(
            "SELECT euq FROM EndUserQueue euq " +
                    "WHERE ((:businessName IS NULL) OR (euq.business.name LIKE %:businessName%))" +
                    "AND ((:queueCode IS NULL) OR (euq.queueCode LIKE %:queueCode%))"+
                    "AND ((:queueName IS NULL) OR (euq.name LIKE %:queueName%))"
    )
    Page<EndUserQueue> getAllQueueInfoFilteredBy(
            @Param("queueCode") String queueCode,
            @Param("businessName") String businessName,
            @Param("queueName") String queueName,
            Pageable pageable
    );




}
