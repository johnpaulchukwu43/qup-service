package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.DuplicateQueueIdException;
import com.jworks.app.commons.exceptions.QueueNotFoundException;
import com.jworks.qup.service.entities.QueueIdSequence;
import com.jworks.qup.service.repositories.QueueIdSequenceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bodmas
 * @since Oct 6, 2021.
 */
@Service
@AllArgsConstructor
@Slf4j
public class QueueIdSequenceGenerator {

    private final QueueIdSequenceRepository queueIdSequenceRepository;

    public void ensureRegistered(long queueId) {
        if (!isQueueRegistered(queueId))
            queueIdSequenceRepository.save(QueueIdSequence.builder().queueId(queueId).build());
    }

    public void registerQueue(long queueId) {
        if (isQueueRegistered(queueId))
            throw new DuplicateQueueIdException(queueId);
        queueIdSequenceRepository.save(QueueIdSequence.builder().queueId(queueId).build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long generateNextJoinId(long queueId) {
        log.debug("Waiting for lock on queue {}", queueId);
        QueueIdSequence queueIdSequence = queueIdSequenceRepository.lockAndSelect(queueId);
        log.debug("Grabbed lock for queue {}", queueId);
        if (queueIdSequence == null)
            throw new QueueNotFoundException(queueId);
        final long nextJoinId = queueIdSequence.getLastJoinId() + 1;
        queueIdSequence.setLastJoinId(nextJoinId);
        log.debug("Generated join id {} for queue {}", nextJoinId, queueId);
        return nextJoinId;
    }

    public boolean isQueueRegistered(long queueId) {
        return queueIdSequenceRepository.existsByQueueId(queueId);
    }
}
