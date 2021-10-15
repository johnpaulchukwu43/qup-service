package com.jworks.qup.service.services;

import static com.jworks.qup.service.providers.EntityProvider.faker;
import java.util.PriorityQueue;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Note: This test is a more general version of {@link QueueTest}
 *
 * @author bodmas
 * @since Dec 28, 2021.
 */
public class Queue2Test {
    /*
    Theory:
    Suppose we know how long it'll take before the pool processors are ready to accept new items, put that in array rt[]
    and we know the average processing time of each pool processor, put that in the array pa[]
    Then the index (G) of the element that waits for a total time T to be processed is given by:
    G <= sum(floor((T - rt[i] + pa[i])/pa[i])) for all i
    Reason: The processing time of every index less than G are of the form rt[i] + pa[i] * q (q >= 0).
    Example:
    Suppose the average processing time of 2 processors in the pool are 2 and 3. And an initial wait time of 5, 9 is respectively required.
    Then the waiting time for each element in the queue is as follows:
    sn- time
    1 - 5
    2 - 7
    3 - 9
    4 - 9
    5 - 11
    6 - 12
    7 - 13
    8 - 15
    9 - 15
    10- 17
    11- 18
    12- 19
    13- 21
    14- 21
    15- 23
    16- 24
    17- 25

    Applying the theory, if we're trying to determine the position in the queue that gives the total waiting time of 18, then we
    work it out as: floor((18 - 5 + 2)/2) + floor((18 - 9 + 3)/3) = 7 + 4 = 11. Which gives the correct answer 11.
    For waiting time 25, we have floor((25 - 5 + 2)/2) + floor((25 - 9 + 3)/3) = 11 + 6 = 17 which is the correct answer.
    For waiting time 24, we have floor((24 - 5 + 2)/2) + floor((24 - 9 + 3)/3) = 10 + 6 = 16 which is the correct answer.
    Next is the ambiguous case 21 because multiple indexes result in this time
    For waiting time 21, we have floor((21 - 5 + 2)/2) + floor((21 - 9 + 3)/3) = 9 + 5 = 14. The correct answers are 13 and 14 but none of them exceed 14, which is in line with the theory.
    */
    @Test
    public void test1() {
        int[] iw = {5, 9}; // Initial wait times.
        int[] a = {2, 3}; // Average waiting times.
//        for (int i = 1; i < 30; i++) {
//            System.out.println("Total time " + i + " is from index " + determine(a, i));
//        }
        /*
        Now given the index, we can assert what expected wait time is using binary search.

        */

//        System.out.println(-3 / 5);
        assertEquals(5, waitTime(1, iw, a));
        assertEquals(7, waitTime(2, iw, a));
        assertEquals(9, waitTime(3, iw, a));
        assertEquals(9, waitTime(4, iw, a));
        assertEquals(11, waitTime(5, iw, a));
        assertEquals(12, waitTime(6, iw, a));
        assertEquals(13, waitTime(7, iw, a));
        assertEquals(15, waitTime(8, iw, a));
        assertEquals(15, waitTime(9, iw, a));
        assertEquals(17, waitTime(10, iw, a));
        assertEquals(18, waitTime(11, iw, a));
        assertEquals(19, waitTime(12, iw, a));
        assertEquals(21, waitTime(13, iw, a));
        assertEquals(21, waitTime(14, iw, a));
        assertEquals(23, waitTime(15, iw, a));
        assertEquals(24, waitTime(16, iw, a));
        assertEquals(25, waitTime(17, iw, a));
    }

    @Test
    public void testGeneral() {
        for (int i = 0; i < 100; i++) {
            final int limit = faker.number().numberBetween(5, 21);
            int[] iw = IntStream.generate(() -> faker.number().numberBetween(1, 51)).limit(limit).toArray();
            int[] a = IntStream.generate(() -> faker.number().numberBetween(1, 51)).limit(limit).toArray();
            int count = faker.number().numberBetween(30, 51);
            final int[] compute = compute(count, iw, a); // Compute results from 1 to 'count'.
            for (int pos = 1; pos <= count; pos++)
                assertEquals(compute[pos - 1], waitTime(pos, iw, a));
        }
    }

    private int determineIndex(int[] iw, int[] a, int time) {
        int sum = 0;
        assert iw.length == a.length;
        for (int i = 0; i < iw.length; i++)
            sum += Math.max(0, (time - iw[i] + a[i])) / a[i];
        return sum;
    }

    // Returns the total time that the item at 'pos' will have to wait.
    private int waitTime(int pos, int[] iw, int[] a) {
        int maxWaitTime = 1_000_000_000;
        int minWaitTime = 0;

        while (maxWaitTime - minWaitTime > 1) {
            int waitTime = minWaitTime + maxWaitTime >> 1;
            int maxIndex = determineIndex(iw, a, waitTime);
            if (maxIndex >= pos)
                maxWaitTime = waitTime;
            else
                minWaitTime = waitTime;
        }
        return maxWaitTime;
    }

    private int[] compute(int count, int[] iw, int[] a) {
        int[] x = new int[count];
        PriorityQueue<int[]> pq = new PriorityQueue<>((p, q) -> p[1] - q[1]); // Min queue sorted by sum.
        for (int i = 0; i < iw.length; i++) {
            pq.add(new int[] {a[i], iw[i]}); // Delta at index 0, sum at index 1.
        }
        for (int i = 0; i < x.length; i++) {
            int[] remove = pq.remove();
            x[i] = remove[1];
            remove[1] += remove[0];
            pq.add(remove);
        }
        return x;
    }
}
