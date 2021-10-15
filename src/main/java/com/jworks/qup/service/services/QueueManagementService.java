package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.EmptyQueueException;
import com.jworks.app.commons.exceptions.NonQueueMemberException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.models.PoolInfo;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Service
@Slf4j
@AllArgsConstructor
public class QueueManagementService {

    private final Sort SORT_BY_JOIN_ID = Sort.by("joinId");

    private final EndUserReservationRepository endUserReservationRepository;

    public boolean hasMembers(long queueId) {
        return endUserReservationRepository.countReservationsMatching(queueId, ReservationStatus.WAITING) > 0;
    }

    /**
     * Called to remove the item in front of this queue and change its state to {@link ReservationStatus#TURN_REACHED}.
     * @param queueId the {@link EndUserQueue#id queue id}
     * @return the next reservation
     * @throws EmptyQueueException if there's no next item
     */
    public EndUserReservation removeNextInLine(long queueId) {
        EndUserReservation endUserReservation = getNextWaitingItem(queueId, true);
        endUserReservation.setReservationStatus(ReservationStatus.TURN_REACHED);
        endUserReservationRepository.save(endUserReservation);
        return endUserReservation;
    }

    private EndUserReservation getNextWaitingItem(long queueId, boolean throwIfNotFound) {
        Iterator<EndUserReservation> iterator = endUserReservationRepository.getReservationsMatching(
                queueId, ReservationStatus.WAITING, PageRequest.of(0, 1, SORT_BY_JOIN_ID)
        ).iterator();
        if (!iterator.hasNext() && throwIfNotFound)
            throw new EmptyQueueException();
        return iterator.hasNext() ? iterator.next() : null;
    }

//    public void logTurnReachedOfAllMembers(PoolInfo poolInfo) {
//        endUserReservationRepository.getAllWaitingItems().forEach(person -> {
//            LocalDateTime localDateTime = computeExpectedTurnReachedTime(poolInfo, person);
//            log.info(person.getJoinId() + " is expected to be admitted to pool at " + localDateTime);
//        });
//    }

    public LocalDateTime computeExpectedTurnReachedTime(PoolInfo poolInfo, EndUserReservation personInQueue) {

        /*
        We have:
        - average processing time of an element in pool
        - Join id of the queue element. This is basically the serial number of this element in this queue.
        - Ordered expected finish time of each element in pool.
         */
        /*
        If pool can accommodate this queue element, then return NOW.
        Else if average waiting time has not been determined, then return UNKNOWN
        Else
         * The pool is fully occupied and the average waiting time has been determined.
        Do
        - Create new list of size 'poolSize', should contain the existing finish times and new entries of finish times whose
        value equal to current time plus average processing time.
        - Calculate the new offset to begin at 1 for the new first element in the queue.
        - Pass finishing times list, average processing time, and ordinal to calculator to compute the expected wait time
        - Return expected wait time.
         */
        LocalDateTime now = LocalDateTime.now();

        long offsetInQueue = personInQueue.getJoinId() - getNextWaitingItem(personInQueue.getEndUserQueue().getId(), true).getJoinId();
        if (offsetInQueue < 0)
            throw new NonQueueMemberException(); // Member on 'queueJoinId' has already been processed and no longer on queue.

        if (poolInfo.getCapacity() - poolInfo.getOrderedExpectedFinishTimesOfOthersInPool().size() > offsetInQueue) {
            // This queue item can be accommodated by the pool.
            return now;
        } else if (poolInfo.getCompletedCount() == 0)
            return null;
        else {
            int remainingItems = poolInfo.getCapacity() - poolInfo.getOrderedExpectedFinishTimesOfOthersInPool().size();
            List<LocalDateTime> newSortedList = new ArrayList<>(poolInfo.getOrderedExpectedFinishTimesOfOthersInPool());
            final Duration averageProcessingTime = poolInfo.getTotalWaitTime().dividedBy(poolInfo.getCompletedCount());
            final LocalDateTime expectedFinishTime = now.plus(averageProcessingTime);
            for (int i = 0; i < remainingItems; i++)
                newSortedList.add(expectedFinishTime);
            assert newSortedList.size() == poolInfo.getCapacity();

            long ordinal = personInQueue.getJoinId() - remainingItems; // 1-indexed.
            return compute(now, newSortedList, averageProcessingTime, ordinal);
        }
    }

    // Computes the total time that the item at 'pos' will have to wait.
    private LocalDateTime compute(LocalDateTime now, List<LocalDateTime> newSortedList, Duration averageProcessingTime, long pos) {
        assertSorted(newSortedList);
        LocalDateTime maxTurnReachedTime = now.plusYears(1); // We imagine no one will have to wait more than a year.
        LocalDateTime minTurnReachedTime = now;

        // Use binary search to determine the total wait time for the element at 'pos'.
        while (Duration.between(minTurnReachedTime, maxTurnReachedTime).toNanos() > 1) {
            LocalDateTime speculation = minTurnReachedTime.plus(Duration.between(minTurnReachedTime, maxTurnReachedTime).dividedBy(2));
            long maxIndex = determineIndex(newSortedList, averageProcessingTime, speculation);
            if (maxIndex >= pos)
                maxTurnReachedTime = speculation;
            else
                minTurnReachedTime = speculation;
        }
        return maxTurnReachedTime;
    }

    private long determineIndex(List<LocalDateTime> iw, Duration averageProcessingTime, LocalDateTime targetDateTime) {
        long sum = 0;
        for (LocalDateTime future : iw) {
            Duration duration = Duration.between(future, targetDateTime).plus(averageProcessingTime);
            if (duration.compareTo(Duration.ZERO) < 0)
                duration = Duration.ZERO;
            sum += duration.toNanos() / averageProcessingTime.toNanos();
        }
        return sum;
    }

    private void assertSorted(List<LocalDateTime> newSortedList) {
        Iterator<LocalDateTime> iterator = newSortedList.iterator();
        if (!iterator.hasNext())
            return;
        LocalDateTime last = iterator.next();
        while (iterator.hasNext()) {
            LocalDateTime current = iterator.next();
            if (last.compareTo(current) > 0)
                throw new RuntimeException("List is not sorted: " + last + ", " + current);
            last = current;
        }
    }
}
