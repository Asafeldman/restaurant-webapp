package com.app.appfrontlocations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
public class ConcurrencyTest {
    private static final int NUM_THREADS = 3;
    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final AtomicInteger successfulThreads = new AtomicInteger(0);

    @Test
    public void addOrUpdateTest() throws InterruptedException {
        LocationDAO locationDAO = new LocationDAO();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        Location testLocation = new Location("TestLocation1", "TestAddress", "Closed");

        Runnable task = () -> {
            try {
                startLatch.await();
                boolean result = locationDAO.addOrUpdateLocation(testLocation);
                if (result) {
                    successfulThreads.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(task);
        }
        startLatch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        assertEquals(1, successfulThreads.get());
    }


}
