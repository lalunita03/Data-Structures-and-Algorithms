package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.TestHint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.util.Queue;
import java.util.Random;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IFixedSizeQueueTests extends IQueueTests {
    IFixedSizeQueue<Object> newFixedSizeQueue(int capacity);

    @Order(fixedSizeQueueLevel)
    @DisplayName("Overflow test for enqueue(...)")
    @TestHint("This test is likely failing because you don't have correct wrap-around behavior.\n     Remember that everything should advance; so, it's possible that your start wraps around.")
    @ParameterizedTest(name = "Test randomly enqueue()ing/dequeue()ing {1} random numbers with seed = {0} and fixed array size = {2}")
    @CsvSource({
            "97, 3000, 100", "38, 5000, 10"
    })
    default void overflowTestEnqueue(int seed, int numVals, int queueSize) {
        Random r = new Random(seed);
        Constructor c = Reflection.getConstructor(CircularArrayFixedSizeQueue.class, int.class);
        IFixedSizeQueue<Object> me = newFixedSizeQueue(queueSize);
        Queue<Object> reference = new java.util.ArrayDeque<>();
        assertEquals(queueSize, me.capacity(), "capacity does not match expected value");
        int count = 0;
        for (int i = 0; i < numVals; i++) {
            int num = r.nextInt();
            // Check that we get the expected value from enqueue when it has a risk of overflowing
            if (count < queueSize) {
                assertEquals(false, me.isFull(), "queue should not be full");
                assertEquals(true, me.enqueue(num), "enqueue should be successful");
                reference.add(num);
                count++;
            }
            else {
                assertEquals(true, me.isFull(), "queue should be full");
                assertEquals(false, me.enqueue(num), "enqueue should have failed");
            }

            // Standard checks to make sure peeks() and dequeues() match up
            assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
            if (r.nextBoolean()) {
                assertEquals(reference.remove(), me.dequeue(),"return values of dequeue()s are not equal");
                assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
                count--;
            }
            assertEquals(reference.size(), me.size(), "size()s are not equal");
            assertEquals(queueSize, me.capacity(), "capacity does not match expected value");
        }
    }
}
