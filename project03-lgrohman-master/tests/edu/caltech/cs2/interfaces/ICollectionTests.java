package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.helpers.DependsOn;
import edu.caltech.cs2.helpers.TestDescription;
import edu.caltech.cs2.interfaces.ICollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface ICollectionTests {
    ICollection<Object> newCollection();

    @Order(collectionTestLevel)
    @DisplayName("Simple tests of various ICollection functions")
    @ParameterizedTest(name = "Test add(), size(), isEmpty(), contains(), and clear() on [{arguments}]")
    @DependsOn({"constructor", "iterator", "size", "addBack"})
    @TestDescription("This test checks the built-in functions we gave you by using your iterator and size methods.")
    @ValueSource(strings = {
            "",
            "1",
            "0, 1, 2, 3",
            "5, 4, 3, 2, 1",
            "8, 3, 5, 7, 4, 3, 12, 12, 1"
    })
    default void testCollectionFunctions(String inputs) {
        ICollection<Object> impl = newCollection();
        List<Object> reference = new java.util.ArrayList<>();
        // Check that collection is empty
        assertTrue(impl.isEmpty(), "collection should be empty");

        // Check that values are not in collection
        for (Object value : inputs.trim().split(", ")) {
            assertFalse(impl.contains(value), "value should not be contained");
        }

        // Add all values to collection
        for (Object value : inputs.trim().split(", ")) {
            impl.add(value);
            reference.add(value);
        }

        // Check that size() and isEmpty() is correct
        assertEquals(reference.size(), impl.size(), "sizes should be equal");
        assertFalse(impl.isEmpty(), "collection should not be empty");

        // Check that values are in collection
        for (Object value : inputs.trim().split(", ")) {
            assertTrue(impl.contains(value), "value should be contained");
        }

        // Clear and make sure size() and isEmpty() match
        impl.clear();
        assertEquals(0, impl.size(), "size should be 0");
        assertTrue(impl.isEmpty(), "collection should be empty");

        // Check that values are not in collection
        for (Object value : inputs.trim().split(", ")) {
            assertFalse(impl.contains(value), "value should not be contained");
        }
    }

    @Order(collectionTestLevel)
    @Test
    @DisplayName("Test repeated emptying and filling of ICollection with single element")
    @DependsOn({"constructor", "size", "addBack", "removeBack"})
    @TestDescription("This test repeatedly adds and clears the data structure.")
    default void testFillEmptyCollection() {
        ICollection<Object> impl = newCollection();
        for (int i = 0; i < 10; i ++) {
            impl.add("a");
            assertEquals(1, impl.size(), "collection should have 1 element");
            impl.clear();
            assertTrue(impl.isEmpty());
        }
    }

    @Order(collectionTestLevel)
    @DisplayName("Stress test for add(...)")
    @ParameterizedTest(name = "Test add()ing {1} random numbers with seed = {0}")
    @DependsOn({"constructor", "size", "addBack", "iterator"})
    @TestDescription("This test adds a ton of random numbers to your data structure and checks that they're all actually in there.")
    @CsvSource({
            "100, 3000", "42, 1000"
    })
    default void stressTestAdd(int seed, int size) {
        Random r = new Random(seed);
        List<Integer> reference = new java.util.ArrayList<>();
        ICollection<Object> impl = newCollection();
        // Test adding values updates size and displays contained correctly
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.add(num);
            impl.add(num);
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.contains(num), impl.contains(num), "value should be contained");
        }
        // Test that values not in collection are not contained
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            assertEquals(reference.contains(num), impl.contains(num), "contained values do not match");
        }
    }

    @Order(collectionTestLevel)
    @DisplayName("Stress test for contains(...)")
    @ParameterizedTest(name = "Test contains() with {1} random numbers and seed = {0}")
    @DependsOn({"constructor", "size", "addBack", "iterator"})
    @TestDescription("This test adds a ton of random numbers to your data structure and checks that they're all actually in there.")
    @CsvSource({
            "100, 3000", "42, 1000"
    })
    default void stressTestContains(int seed, int size) {
        Random r = new Random(seed);
        List<Integer> nums = new java.util.ArrayList<>();
        ICollection<Object> impl = newCollection();
        // Add values to both the list of nums and test collection
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            nums.add(num);
            impl.add(num);
        }
        // Shuffle order of nums and check that all are contained in the collection
        Collections.shuffle(nums);
        for (int num : nums) {
            assertTrue(impl.contains(num), "value should be contained");
        }

        // Test that values not in collection are not contained
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            assertEquals(nums.contains(num), impl.contains(num), "contained values do not match");
        }
    }
}
