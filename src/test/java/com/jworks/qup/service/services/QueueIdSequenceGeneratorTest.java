package com.jworks.qup.service.services;

import com.jworks.qup.service.config.QueueIdSequenceGeneratorConfig;
import com.jworks.qup.service.misc.Utils;
import com.jworks.qup.service.repositories.QueueIdSequenceRepository;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Tests for {@link QueueIdSequenceGenerator}.
 *
 * @author bodmas
 * @since Oct 8, 2021.
 */
@Import(QueueIdSequenceGeneratorConfig.class)
public class QueueIdSequenceGeneratorTest extends AbstractServiceTest {

    @Autowired
    private QueueIdSequenceGenerator queueIdSequenceGenerator;
    @Autowired
    private QueueIdSequenceRepository queueIdSequenceRepository;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        queueIdSequenceRepository.deleteAll();
    }

    @Test
    public void registerQueueShouldWork() {
        final long queueId = 1L;
        queueIdSequenceGenerator.registerQueue(queueId);
        assertThat(queueIdSequenceRepository.existsByQueueId(queueId)).isTrue();
    }

    @Test
    public void generateNextJoinIdShouldWork() throws InterruptedException {
        // Call the generateNextJoinId method from a number of threads running in parallel.
        // Then check that unique and sequential joinIds were generated.
        final long queueId = 1L;
        queueIdSequenceGenerator.registerQueue(queueId);

        List<Long> joinIds = generateJoinIdsInParallel(queueId, 5);
        joinIds.forEach(System.out::println);
        assertThat(Utils.isSequential(joinIds)).isTrue();
    }

    private List<Long> generateJoinIdsInParallel(final long queueId, final int numThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = executorService.invokeAll(
                Stream.generate(() -> new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return queueIdSequenceGenerator.generateNextJoinId(queueId);
                    }
                }).limit(numThreads).collect(Collectors.toList())
        );
        List<Long> joinIds = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toList());
        return joinIds;
    }
}
