package com.jworks.qup.service.services;

import com.jworks.app.commons.models.PoolInfo;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import com.jworks.qup.service.misc.Utils;
import com.jworks.qup.service.models.PoolGraduateData;
import static com.jworks.qup.service.providers.EntityProvider.faker;
import com.jworks.qup.service.providers.impl.EndUserQueueProvider;
import com.jworks.qup.service.providers.impl.EndUserReservationProvider;
import com.jworks.qup.service.repositories.EndUserReservationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link QueueManagementService}.
 *
 * @author bodmas
 * @since Oct 11, 2021.
 */
public class QueueManagementServiceTest extends AbstractServiceTest {

    private QueueManagementService queueManagementService;

    @Autowired
    private EndUserReservationRepository endUserReservationRepository;
    @Autowired
    private EndUserReservationProvider endUserReservationProvider;
    @Autowired
    private EndUserQueueProvider endUserQueueProvider;

    @BeforeEach
    public void setUp() {
        queueManagementService = new QueueManagementService(endUserReservationRepository);
    }

    @AfterEach
    public void tearDown() {
        clear(Arrays.asList(() -> endUserReservationProvider.deleteAll()));
    }

    @Test
    public void hasMembersShouldWork() {
        EndUserQueue endUserQueue = endUserQueueProvider.provideAndSave();
        assertThat(queueManagementService.hasMembers(endUserQueue.getId())).isFalse();

        EndUserReservation endUserReservation = endUserReservationProvider.provide();
        endUserReservation.setEndUserQueue(endUserQueue);
        endUserReservation.setReservationStatus(ReservationStatus.WAITING);
        endUserReservationProvider.save(endUserReservation);

        assertThat(queueManagementService.hasMembers(endUserQueue.getId())).isTrue();

        endUserReservation.setReservationStatus(ReservationStatus.RECEIVING_SERVICE);
        endUserReservationProvider.save(endUserReservation);

        assertThat(queueManagementService.hasMembers(endUserQueue.getId())).isFalse();
    }

    @Test
    public void removeNextInLineShouldWork() {
        EndUserReservation firstEndUserReservation = endUserReservationProvider.provideAndSave();
        final EndUserQueue endUserQueue = firstEndUserReservation.getEndUserQueue();
        final int numReservations = 5;
        // Create some more reservations.
        LongStream.rangeClosed(2, numReservations).forEach(joinId -> {
            EndUserReservation endUserReservation = endUserReservationProvider.provide();
            endUserReservation.setEndUserQueue(endUserQueue);
            endUserReservationProvider.save(endUserReservation);
        });
        EndUserReservation nextInLine = queueManagementService.removeNextInLine(endUserQueue.getId());
        assertThat(endUserReservationRepository.countByEndUserQueueId(endUserQueue.getId())).isEqualTo(numReservations);
        assertThat(nextInLine.getId()).isEqualTo(firstEndUserReservation.getId());
        assertThat(nextInLine.getReservationStatus()).isEqualTo(ReservationStatus.TURN_REACHED);
    }

    @Test
    public void computeExpectedTurnReachedTimeShouldWork() {
        final int poolSize = 1 + random.nextInt(20);
        final int numReservations = 1 + poolSize + random.nextInt(50);

        PoolTimeService poolTimeService = new PoolTimeService(poolSize);
        EndUserQueue endUserQueue = endUserQueueProvider.provideAndSave();

        List<EndUserReservation> endUserReservations = generateReservations(endUserQueue, numReservations);
        List<LocalDateTime> expectedTurnReachedTimes = computeExpectedTurnReachedTimes(endUserReservations, poolTimeService);

        // Ascertain that the assigned join ids are distinct and sequential.
        assertThat(Utils.isSequential(endUserReservations.stream().map(EndUserReservation::getJoinId).collect(Collectors.toList())));
        // Ascertain that #items from the front of the queue that are immediately admissible to the pool is equal to the pool size
        assertThat(expectedTurnReachedTimes.stream().limit(poolSize).filter(Objects::nonNull).count()).isEqualTo(poolSize);
        // Ascertain that the remaining items in the queue have an undecided wait time.
        assertThat(expectedTurnReachedTimes.stream().skip(poolSize).filter(Objects::isNull).count()).isEqualTo(numReservations - poolSize);

        Set<Long> itemsInPool = new HashSet<>();
        int totalSentToPool = 0;
        int interations = 0;

        while (totalSentToPool != numReservations) {
            if (++interations > 1000)
                throw new RuntimeException("Too many iterations");

            List<EndUserReservation> reservationsSentToPool = removeFromQueueAndAdmitToPool(
                    endUserQueue, poolTimeService,
                    faker.number().numberBetween(0, 1 + Math.min(numReservations - totalSentToPool, getAllowance(poolTimeService)))
            );
            itemsInPool.addAll(reservationsSentToPool.stream().map(EndUserReservation::getJoinId).collect(Collectors.toList()));

            // Ascertain that the reservations that were sent to the pool were the ones at the front of the queue.
            assertThat(reservationsSentToPool).usingElementComparator(Comparator.comparing(EndUserReservation::getJoinId))
                    .isEqualTo(endUserReservations.subList(totalSentToPool, totalSentToPool + reservationsSentToPool.size()));

            totalSentToPool += reservationsSentToPool.size();

            // Remove an item from pool and assert that next item in queue is now eligible for admission to the pool.
            List<Long> itemsToGraduate = randomItemsFromPool(itemsInPool);
            for (long itemToGraduate : itemsToGraduate) {
                PoolGraduateData graduate = poolTimeService.graduate(itemToGraduate);
                itemsInPool.remove(itemToGraduate);
            }

            List<LocalDateTime> newExpectedTurnReachedTimeOfOthers = computeExpectedTurnReachedTimes(
                    endUserReservations.stream().skip(totalSentToPool).collect(Collectors.toList()), poolTimeService
            );
            // Update expected turn reached times.
            for (int i = totalSentToPool; i < endUserReservations.size(); i++)
                expectedTurnReachedTimes.set(i, newExpectedTurnReachedTimeOfOthers.get(i - totalSentToPool));

            if (poolTimeService.getPoolInfo().getCompletedCount() == 0) { // Average processing time not yet determined.
                assert totalSentToPool <= poolSize;
                assertThat(newExpectedTurnReachedTimeOfOthers.subList(0, poolSize - totalSentToPool)).allMatch(Objects::nonNull);
                assertThat(newExpectedTurnReachedTimeOfOthers.stream()
                        .skip(poolSize - totalSentToPool).collect(Collectors.toList())).allMatch(Objects::isNull);
            } else // Average processing time has been determined.
                assertThat(newExpectedTurnReachedTimeOfOthers).doesNotContainNull();

            expectedTurnReachedTimes.forEach(System.out::println);
            // Ascertain that every item's turn should be reached before the item after it.
            for (int i = 1; i < expectedTurnReachedTimes.size() && expectedTurnReachedTimes.get(i) != null; i++)
                Assertions.assertThat(expectedTurnReachedTimes.get(i - 1)).isBeforeOrEqualTo(expectedTurnReachedTimes.get(i));
        }
    }

    private List<LocalDateTime> computeExpectedTurnReachedTimes(List<EndUserReservation> endUserReservations,
                                                                PoolTimeService poolTimeService) {
        return endUserReservations.stream().map(endUserReservation -> {
            try {
                // Sleep a bit. This is because System.currentTimeMillis is not monotonic, and during the tests, we fire
                // several calls within very close nanoseconds. Sleeping allows us mitigate the manifestation so that
                // we can assert the ordering of the expected turn reached times. In particular we want to make sure that
                // if person A comes before person B on the queue, then it doesn't get to person B's turn before person A.
                TimeUnit.MILLISECONDS.sleep(10); // Increase the sleep time if the current setting is too small.
            } catch (InterruptedException ex) {
            }
            LocalDateTime expectedTurnReachedTime = queueManagementService.computeExpectedTurnReachedTime(
                    poolTimeService.getPoolInfo(), endUserReservation
            );
            return expectedTurnReachedTime;
        }).collect(Collectors.toList());
    }

    private List<EndUserReservation> generateReservations(EndUserQueue endUserQueue, int numReservations) {
        // Make a number of reservations.
        List<EndUserReservation> endUserReservations = Stream.generate(() -> {
            EndUserReservation endUserReservation = endUserReservationProvider.provide();
            endUserReservation.setEndUserQueue(endUserQueue);
            return endUserReservationProvider.save(endUserReservation);
        }).limit(numReservations).collect(Collectors.toList());
        return endUserReservations;
    }

    private List<EndUserReservation> removeFromQueueAndAdmitToPool(EndUserQueue endUserQueue, PoolTimeService poolTimeService, int count) {
        List<EndUserReservation> endUserReservations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EndUserReservation endUserReservation = queueManagementService.removeNextInLine(endUserQueue.getId());
            poolTimeService.admit(endUserReservation.getJoinId());
            endUserReservations.add(endUserReservation);
        }
        return endUserReservations;
    }

    // Returns the number of items required for pool to reach its capacity.
    private int getAllowance(PoolTimeService poolTimeService) {
        final PoolInfo poolInfo = poolTimeService.getPoolInfo();
        return poolInfo.getCapacity() - poolInfo.getOrderedExpectedFinishTimesOfOthersInPool().size();
    }

    private List<Long> randomItemsFromPool(Set<Long> itemsInPool) {
        int removeCount = faker.number().numberBetween(0, 1 + itemsInPool.size());
        Set<Integer> removals = new HashSet<>(chooseAtRandom(removeCount, itemsInPool.size()));
        List<Long> toRemove = new ArrayList<>();
        List<Long> itemsInPoolList = new ArrayList<>(itemsInPool);
        for (int i = 0; i < itemsInPoolList.size(); i++)
            if (removals.contains(i))
                toRemove.add(itemsInPoolList.get(i));
        return toRemove;
    }
}
