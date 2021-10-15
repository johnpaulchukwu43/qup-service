package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.CapacityReachedException;
import com.jworks.app.commons.models.PersonInPool;
import com.jworks.app.commons.models.PoolInfo;
import com.jworks.qup.service.models.PoolGraduateData;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Allows clients to track the time each id spends in the pool, as well as track average time an id
 * spends in pool before they graduate.
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Slf4j
public class PoolTimeService {

    private int completedCount;
    private Duration totalWaitTime = Duration.ZERO;
    private final int capacity;
    private final TreeSet<PersonInPool> pool = new TreeSet<>(Comparator.comparing(PersonInPool::getId));

    public PoolTimeService(int capacity) {
        this.capacity = capacity;
    }

    private int getCapacity() {
        return capacity;
    }

    public void admit(long joinId) {
        log.info(LocalDateTime.now() + ": admitting " + joinId + " to pool");
        synchronized (this) {
            if (getPoolSize() >= getCapacity())
                throw new CapacityReachedException();
            addElement(joinId);
        }
    }

    public boolean canAdmit() {
        synchronized (this) {
            return getPoolSize() < getCapacity();
        }
    }

    /**
     * Removes the item having the given {@code joinId} from the pool.
     * @param joinId
     * @return the data regarding the item with the given {@code joinId}, or null if not found
     */
    public PoolGraduateData graduate(long joinId) {
        log.info("{}: graduating {} from pool", LocalDateTime.now(), joinId);

        synchronized (this) {
            LocalDateTime finishTime = LocalDateTime.now();
            log.info(joinId + " got lock in pool at " + finishTime);
            PersonInPool personInPool = removeElement(joinId);
            if (personInPool == null)
                return null;
            personInPool.setFinishTime(finishTime);
            return PoolGraduateData.builder().graduate(personInPool).poolInfo(getPoolInfo()).build();
        }
    }

    public PoolInfo getPoolInfo() {
        synchronized (this) {
            return new PoolInfo(
                    getCapacity(), getCompletedCount(), getTotalWaitTime(),
                    getExpectedFinishingTimesInOrder()
            );
        }
    }

    private int getPoolSize() {
        return pool.size();
    }

    private boolean addElement(long id) {
        PersonInPool personInPool = new PersonInPool(id);
        personInPool.setJoinTime(LocalDateTime.now());
        return pool.add(personInPool);
    }

    private PersonInPool findElement(long id) {
        PersonInPool ceiling = pool.ceiling(new PersonInPool(id));
        return ceiling != null && ceiling.getId() == id ? ceiling : null;
    }

    // Computation expects that items will finish in the order in which they joined the pool. This might not be the reality
    // of things but this is the expectation.
    private List<LocalDateTime> getExpectedFinishingTimesInOrder() { // Increasing order.
        if (completedCount == 0) // Set each finishing time to null indicating that finishing time is not yet known.
            return Stream.generate(() -> (LocalDateTime) null).limit(pool.size()).collect(Collectors.toList());

        List<LocalDateTime> orderedPersonInPoolFinishTimes = new ArrayList<>();
        LocalDateTime lastTime = null;
        Iterator<PersonInPool> iterator = pool.iterator();
        Duration averageProcessingTime = totalWaitTime.dividedBy(completedCount);
        while (iterator.hasNext()) {
            PersonInPool personInPool = iterator.next();
            if (lastTime == null || personInPool.getJoinTime().plus(averageProcessingTime).compareTo(lastTime) > 0)
                lastTime = personInPool.getJoinTime().plus(averageProcessingTime);
            orderedPersonInPoolFinishTimes.add(lastTime);
        }
        return orderedPersonInPoolFinishTimes;
    }

    /**
     *
     * @param id the id of the person to find
     * @return the item having this id if found in pool, otherwise null
     */
    private PersonInPool removeElement(long id) {
        LocalDateTime finishTime = LocalDateTime.now();
        PersonInPool personInPool = findElement(id);
        if (personInPool == null)
            return null;

        Duration timeSpentInPool = Duration.between(personInPool.getJoinTime(), finishTime);
        boolean removed = pool.remove(personInPool);
        if (removed)
            increment(timeSpentInPool);
        return removed ? personInPool : null;
    }

    private void increment(Duration timeSpentInPool) {
        totalWaitTime = totalWaitTime.plus(timeSpentInPool);
        completedCount++;
    }

    private int getCompletedCount() {
        return completedCount;
    }

    private Duration getTotalWaitTime() {
        return totalWaitTime;
    }
}
