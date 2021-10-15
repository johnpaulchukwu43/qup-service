package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.QueueIdSequence;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * @author bodmas
 * @since Oct 6, 2021.
 */
@Repository
public interface QueueIdSequenceRepository extends BaseRepository<QueueIdSequence> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT x FROM #{#entityName} x WHERE x.queueId = :queueId")
    QueueIdSequence lockAndSelect(long queueId);

    boolean existsByQueueId(long queueId);
}
