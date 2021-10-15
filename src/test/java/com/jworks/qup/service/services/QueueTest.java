package com.jworks.qup.service.services;

import static com.jworks.qup.service.providers.EntityProvider.faker;
import java.util.PriorityQueue;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author bodmas
 * @since Dec 28, 2021.
 */
public class QueueTest {
    /*
    Theory:
    Suppose we know the average processing time of each pool processor, put that in the array pa[]
    Then the index (G) of the element that waits for a total time T to be processed does not exceed sum(floor(T/pa[i])) for all i
    i.e. G <= sum(floor(T/pa[i])) for all i
    Reason: All multiples of pa[i] which are less than T will be processed before the time T. Also every index less than G
    would be processed before G and will be a multiple of some 'pa'.
    Example:
    Suppose the average processing time of 2 processors in the pool are 2 and 7.
    Then the waiting time for each element in the queue is as follows:
    sn- time
    1 - 2
    2 - 4
    3 - 6
    4 - 7
    5 - 8
    6 - 10
    7 - 12
    8 - 14
    9 - 14
    10- 16
    11- 18
    12- 20
    13- 21
    14- 22

    Applying the theory, if we're trying to determine the position in the queue that gives the total waiting time of 18, then we
    work it out as: floor(18/2) + floor(18/7) = 9 + 2 = 11. Which gives the correct answer 11.
    For waiting time 22, we have floor(22/2) + floor(22/7) = 11 + 3 = 14 which is the correct answer.
    For waiting time 12, we have floor(12/2) + floor(12/7) = 6 + 1 = 7 which is the correct answer.
    Next is the ambiguous case 14 because multiple indexes result in this time
    For waiting time 14, we have floor(14/2) + floor(14/7) = 7 + 2 = 9. The correct answers are 8 and 9 but none of them exceed 9, which is in line with the theory.
    */
    @Test
    public void test1() {
        int[] a = {2, 7};
//        for (int i = 1; i < 30; i++) {
//            System.out.println("Total time " + i + " is from index " + determine(a, i));
//        }
        /*
        Now given the index, we can assert what expected wait time is using binary search.
        */

        assertEquals(2, waitTime(1, a));
        assertEquals(4, waitTime(2, a));
        assertEquals(6, waitTime(3, a));
        assertEquals(7, waitTime(4, a));
        assertEquals(8, waitTime(5, a));
        assertEquals(10, waitTime(6, a));
        assertEquals(12, waitTime(7, a));
        assertEquals(14, waitTime(8, a));
        assertEquals(14, waitTime(9, a));
        assertEquals(16, waitTime(10, a));
        assertEquals(18, waitTime(11, a));
        assertEquals(20, waitTime(12, a));
        assertEquals(21, waitTime(13, a));
        assertEquals(22, waitTime(14, a));
    }

    @Test
    public void test2() {
        int[] a = {2, 3};
        assertEquals(2, waitTime(1, a));
        assertEquals(3, waitTime(2, a));
        assertEquals(4, waitTime(3, a));
        assertEquals(6, waitTime(4, a));
        assertEquals(6, waitTime(5, a));
        assertEquals(8, waitTime(6, a));
        assertEquals(9, waitTime(7, a));
        assertEquals(10, waitTime(8, a));
        assertEquals(12, waitTime(9, a));
        assertEquals(12, waitTime(10, a));
    }

    @Test
    public void testGeneral() {
        for (int i = 0; i < 100; i++) {
            int[] a = IntStream.generate(() -> faker.number().numberBetween(1, 51)).limit(faker.number().numberBetween(5, 21)).toArray();
            int count = faker.number().numberBetween(30, 51);
            final int[] compute = compute(count, a); // Compute results from 1 to 'count'.
            for (int pos = 1; pos <= count; pos++)
                assertEquals(compute[pos - 1], waitTime(pos, a));
        }
    }

    private int determine(int[] a, int time) {
        int sum = 0;
        for (int x : a)
            sum += time / x;
        return sum;
    }

    // Returns the total time that the item at 'pos' will have to wait.
    private int waitTime(int pos, int[] a) {
        int maxWaitTime = 1_000_000_000;
        int minWaitTime = 0;

        while (maxWaitTime - minWaitTime > 1) {
            int waitTime = minWaitTime + maxWaitTime >> 1;
            int maxIndex = determine(a, waitTime);
            if (maxIndex >= pos)
                maxWaitTime = waitTime;
            else
                minWaitTime = waitTime;
        }
        return maxWaitTime;
    }

    private int[] compute(int count, int[] a) {
        int[] x = new int[count];
        PriorityQueue<int[]> pq = new PriorityQueue<>((p, q) -> p[1] - q[1]); // Min queue sorted by sum.
        for (int y : a)
            pq.add(new int[] {y, y}); // Original at index 0, sum at index 1.
        for (int i = 0; i < x.length; i++) {
            int[] remove = pq.remove();
            x[i] = remove[1];
            remove[1] += remove[0];
            pq.add(remove);
        }
        return x;
    }
}
