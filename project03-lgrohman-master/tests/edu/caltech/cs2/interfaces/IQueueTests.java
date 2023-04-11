package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Queue;
import java.util.Random;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static org.junit.jupiter.api.Assertions.*;

public interface IQueueTests {
    IQueue<Object> newQueue();
    IQueue<Object> newQueue(int size);

    @Order(queueTestLevel)
    @DisplayName("Stress test for enqueue(...) and peek(...)")
    @ParameterizedTest(name = "Test enqueue()ing {1} random numbers with seed = {0}")
    @CsvSource({
            "97, 3000", "38, 5000"
    })
    default void stressTestEnqueue(int seed, int size) {
        Random r = new Random(seed);
        Queue<Object> reference = new java.util.ArrayDeque<>();
        IQueue<Object> me = newQueue(size);
        // Test that first peek is null
        assertNull(me.peek(), "empty peek should return null");
        // Test adding values updates size and peek correctly
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.add(num);
            assertTrue(me.enqueue(num), "enqueue should be successful");
            assertEquals(reference.size(), me.size(), "size()s are not equal");
            assertEquals(reference.peek(), me.peek(), "peeks should be the same");
        }
    }

    @Order(queueTestLevel)
    @DisplayName("Stress test for dequeue(...)")
    @ParameterizedTest(name = "Test dequeue()ing {1} random numbers with seed = {0}")
    @CsvSource({
            "98, 3000", "39, 5000"
    })
    default void stressTestDequeue(int seed, int size) {
        Random r = new Random(seed);
        Queue<Object> reference = new java.util.ArrayDeque<>();
        IQueue<Object> me = newQueue(size);
        // Test that first dequeue is null
        assertNull(me.dequeue(), "empty dequeue should return null");
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.add(num);
            assertTrue(me.enqueue(num), "enqueue should be successful");
            assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
            if (r.nextBoolean()) {
                assertEquals(reference.remove(), me.dequeue(),"return values of dequeue()s are not equal");
                assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
            }
            assertEquals(reference.size(), me.size(), "size()s are not equal");
        }
    }
}
