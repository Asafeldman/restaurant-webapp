package com.app.appfrontlocations;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
public class ConcurrencyTest {
    private int numThreads;
    private int namesLen;
    private LocationDAO locationDAO;
    private CountDownLatch startLatch;
    private ExecutorService executorService;
    private String locationName;
    private String addressName;


    @BeforeEach
    public void setUp() {
        numThreads = RandomUtils.nextInt(1, 20);
        namesLen = RandomUtils.nextInt(1, 10);
        locationDAO = new LocationDAO();
        startLatch = new CountDownLatch(1);
        executorService = Executors.newFixedThreadPool(numThreads);
        locationName = RandomStringUtils.randomAlphabetic(namesLen);
        addressName = RandomStringUtils.randomAlphabetic(namesLen);
    }

    @AfterEach
    public void tearDown() {
        executorService.shutdown();
        try {
            boolean allTasksCompleted = executorService.awaitTermination(60, TimeUnit.SECONDS);
            assertTrue(allTasksCompleted, "Not all tasks have completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        locationDAO.dropLocationCollection();
        System.out.println("-------------------------------------");
    }

    private Runnable updateLocationTask(LocationDAO locationDAO, Location location) {
        return () -> {
            try {
                startLatch.await();
                locationDAO.addOrUpdateLocation(location);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    public void printTestInfo(int testNumber, boolean includeName) {
        System.out.println("Test " + testNumber);
        System.out.println("Number of threads: " + numThreads);
        if (includeName) {
            System.out.println("Initial location name: " + locationName);
            System.out.println("Initial address name: " + addressName);
        }

    }

    @Test
    @Order(1)
    public void multipleAddressUpdateTest() {
        printTestInfo(1, true);
        Location testLocation = new Location(locationName, addressName, "Open");
        locationDAO.addOrUpdateLocation(testLocation);
        for (int i = 0; i < numThreads; i++) {
            String randomAddress = RandomStringUtils.randomAlphabetic(namesLen);
            System.out.println("Thread " + (i + 1) + ": random address is " + randomAddress);
            Location updatedLocation = new Location(locationName, randomAddress, "Open");
            Runnable task = updateLocationTask(locationDAO, updatedLocation);
            executorService.submit(task);
        }
        startLatch.countDown();
        try {
            assertEquals(1, locationDAO.getNumLocations());
            System.out.println("Test 1 passed: location updated correctly with no abundant inserts.");
        } catch (AssertionError e) {
            fail("Test 1 failed: number of locations is not 1.");
        }
    }

    @Test
    @Order(2)
    public void multipleInsertTest() {
        printTestInfo(2, false);
        for (int i = 0; i < numThreads; i++) {
            String randomAddress = RandomStringUtils.randomAlphabetic(namesLen);
            System.out.println("Thread " + (i + 1) + ": random address is " + randomAddress);
            Location updatedLocation = new Location(locationName, randomAddress, "Open");
            Runnable task = updateLocationTask(locationDAO, updatedLocation);
            executorService.submit(task);
        }
        startLatch.countDown();
        try {
            assertEquals(1, locationDAO.getNumLocations());
            System.out.println("Test 2 passed: only 1 location inserted.");
        } catch (AssertionError e) {
            fail("Test 2 failed: number of locations is not 1.");
        }
    }
}
