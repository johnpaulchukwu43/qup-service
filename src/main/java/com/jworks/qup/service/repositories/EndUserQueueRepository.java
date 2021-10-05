package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import com.jworks.qup.service.models.EndUserQueueDto;
import com.jworks.qup.service.models.EndUserQueueInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;


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
                    " AND ((:createdOn IS NULL) OR (euq.createdAt = :createdOn))" +
                    " AND ((:expiryDate IS NULL) OR (euq.expirationDate = :expiryDate))" +
                    " AND ((:queuePurpose IS NULL) OR (euq.queuePurpose = :queuePurpose))"+
                    " AND (euq.endUser.userReference = :userReference)"

    )
    Page<EndUserQueue> getAllQueuesByUserReferenceFilteredBy(
            @Param("queueStatus") QueueStatus queueStatus,
            @Param("queueCode") String queueCode,
            @Param("createdOn") Timestamp createdOn,
            @Param("queuePurpose") QueuePurpose queuePurpose,
            @Param("userReference") String userReference,
            @Param("expiryDate") Timestamp expiryDate,
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
