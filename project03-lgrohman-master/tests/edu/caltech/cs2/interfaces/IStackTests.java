package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.interfaces.IStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Deque;
import java.util.Random;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public interface IStackTests {
    IStack<Object> newStack();

    @Order(stackTestLevel)
    @DisplayName("Stress test for push(...) and peek(...)")
    @ParameterizedTest(name = "Test push()ing {1} random numbers with seed = {0}")
    @CsvSource({
            "99, 3000", "40, 5000"
    })
    default void stressTestPush(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new java.util.ArrayDeque<>();
        IStack<Object> me = newStack();
        // Test that first peek is null
        assertNull(me.peek(), "empty peek should return null");
        // Test adding values updates size and peek correctly
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.push(num);
            me.push(num);
            assertEquals(reference.size(), me.size(), "size()s are not equal");
            assertEquals(reference.peek(), me.peek(), "peeks should be the same");
        }
    }

    @Order(stackTestLevel)
    @DisplayName("Stress test for pop(...)")
    @ParameterizedTest(name = "Test pop()ing {1} random numbers with seed = {0}")
    @CsvSource({
            "98, 3000", "39, 5000"
    })
    default void stressTestPop(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new java.util.ArrayDeque<>();
        IStack<Object> me = newStack();
        // Test that first pop is null
        assertNull(me.pop(), "empty pop should return null");
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.push(num);
            me.push(num);
            assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
            if (r.nextBoolean()) {
                assertEquals(reference.pop(), me.pop(),"return values of pop()s are not equal");
                assertEquals(reference.peek(), me.peek(),"return values of peek()s are not equal");
            }
            assertEquals(reference.size(), me.size(), "size()s are not equal");
        }
    }
}
