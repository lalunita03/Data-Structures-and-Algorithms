package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.helpers.DependsOn;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.TestDescription;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyIterable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public interface IDequeTests {
    IDeque<Object> newDeque();

    @Order(0)
    @DisplayName("toString() matches java.util.ArrayDeque")
    @DependsOn({"fields", "constructors", "toString", "addBack"})
    @ParameterizedTest(name = "Test toString() on [{arguments}]")
    @ValueSource(strings = {
            "0, 1, 2, 3", "5, 4, 3, 2, 1", "8, 3, 5, 7, 4, 3, 12, 12, 1"
    })
    default void testToString(String inputs) {
        java.util.ArrayDeque<String> reference = new java.util.ArrayDeque<String>();
        edu.caltech.cs2.interfaces.IDeque<Object> me = newDeque();
        for (String value : inputs.trim().split(", ")) {
            assertEquals(reference.toString(), me.toString(), "toString outputs should be the same");
            reference.addLast(value);
            me.addBack(value);
        }
    }


    @Test
    @Order(1)
    @DependsOn({"constructor", "iterator", "size", "addFront", "removeFront"})
    @TestDescription("This test checks that small deques work correctly when added at the front and removed at the front.")
    @DisplayName("Test Deque addFront / removeFront edge cases")
    default void testRepeatedAddFrontRemoveFront() {
        ArrayDeque<Object> ref = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 5; j ++) {
                impl.addFront(i);
                ref.addFirst(i);
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
            }
            for (int j = 0; j < 5; j ++) {
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
                impl.removeFront();
                ref.removeFirst();
            }
            assertTrue(impl.isEmpty());
            MatcherAssert.assertThat(impl, IsEmptyIterable.emptyIterable());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test Deque addFront / removeBack edge cases")
    @DependsOn({"constructor", "iterator", "size", "addFront", "removeBack"})
    @TestDescription("This test checks that small deques work correctly when added at the front and removed at the back.")
    default void testRepeatedAddFrontRemoveBack() {
        ArrayDeque<Object> ref = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 5; j ++) {
                impl.addFront(i);
                ref.addFirst(i);
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
            }
            for (int j = 0; j < 5; j ++) {
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
                impl.removeBack();
                ref.removeLast();
            }
            assertTrue(impl.isEmpty());
            MatcherAssert.assertThat(impl, IsEmptyIterable.emptyIterable());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Deque addBack / removeFront edge cases")
    @DependsOn({"constructor", "iterator", "size", "addBack", "removeFront"})
    @TestDescription("This test checks that small deques work correctly when added at the back and removed at the front.")
    default void testRepeatedAddBackRemoveFront() {
        ArrayDeque<Object> ref = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 5; j ++) {
                impl.addBack(i);
                ref.addLast(i);
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
            }
            for (int j = 0; j < 5; j ++) {
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
                impl.removeFront();
                ref.removeFirst();
            }
            assertTrue(impl.isEmpty());
            MatcherAssert.assertThat(impl, IsEmptyIterable.emptyIterable());
        }
    }

    @Test
    @Order(4)
    @DependsOn({"constructor", "iterator", "size", "addBack", "removeBack"})
    @TestDescription("This test checks that small deques work correctly when added at the back and removed at the back.")
    @DisplayName("Test Deque addBack / removeBack edge cases")
    default void testRepeatedAddBackRemoveBack() {
        ArrayDeque<Object> ref = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 5; j ++) {
                impl.addBack(i);
                ref.addLast(i);
                MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
            }
            for (int j = 0; j < 5; j ++) {
                impl.removeBack();
                ref.removeLast();
            }
            assertTrue(impl.isEmpty());
            MatcherAssert.assertThat(impl, IsEmptyIterable.emptyIterable());
        }
    }


    @Order(5)
    @DisplayName("Test Deque Iterator")
    @ParameterizedTest(name = "Test deque iterator on [{arguments}]")
    @DependsOn({"constructor", "iterator", "size", "addBack", "removeBack"})
    @TestDescription("This test checks that your iterator works on various sizes of data structures.")
    @ValueSource(strings = {
            "",
            "1",
            "0, 1, 2, 3",
            "5, 4, 3, 2, 1",
            "8, 3, 5, 7, 4, 3, 12, 12, 1"
    })
    default void testIterator(String inputs) {
        ArrayDeque<Object> ref = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();

        for (Object value : inputs.trim().split(", ")) {
            impl.addBack(value);
            ref.addLast(value);
            MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
        }
        // Check that iterator is consistent while objects are removed from the back
        for (Object ignored : inputs.trim().split(", ")) {
            MatcherAssert.assertThat(impl, IsIterableContainingInOrder.contains(ref.toArray()));
            impl.removeBack();
            ref.removeLast();
        }
        MatcherAssert.assertThat(impl, IsEmptyIterable.emptyIterable());
    }

    @Order(6)
    @DisplayName("Test for addAll(...)")
    @ParameterizedTest(name = "Test addAll with {1} random numbers and seed = {0}")
    @DependsOn({"constructor", "iterator", "addBack", "toString"})
    @TestDescription("This test checks that the built-in addAll method works using your implementation of the iterator and addBack.")
    @CsvSource({
            "99, 300", "48, 500"
    })
    default void testAddAll(int seed, int size) {
        Random r = new Random(seed);
        ICollection<Object> coll = newDeque();
        Object[] expected = new Object[size];
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            coll.add(num);
            expected[i] = num;
        }
        impl.addAll(coll);

        MatcherAssert.assertThat("IDeque has incorrect elements / order", impl, IsIterableContainingInOrder.contains(expected));
    }

    @Order(7)
    @DisplayName("Stress test for addFront(...) and peekFront(...)")
    @ParameterizedTest(name = "Test addFront()ing {1} random numbers with seed = {0}")
    @DependsOn({"constructor", "iterator", "size", "peekFront", "addFront", "toString"})
    @TestDescription("This test checks uses large amounts of random data to test that your adds work correctly.")
    @CsvSource({
            "100, 300", "42, 500"
    })
    default void stressTestAddFront(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        // Test that first peek is null
        assertNull(impl.peekFront(), "empty peek should return null");
        // Test adding values updates size and displays contained correctly
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.addFirst(num);
            impl.addFront(num);
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.peekFirst(), impl.peekFront(), "peeks should be the same");
            assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
        }
    }

    @Order(8)
    @DisplayName("Stress test for addBack(...) and peekBack(...)")
    @DependsOn({"constructor", "iterator", "size", "peekBack", "addBack", "toString"})
    @TestDescription("This test checks uses large amounts of random data to test that your adds work correctly.")
    @ParameterizedTest(name = "Test addBack()ing {1} random numbers with seed = {0}")
    @CsvSource({
            "100, 300", "42, 500"
    })
    default void stressTestAddBack(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        // Test that first peek is null
        assertNull(impl.peekBack(), "empty peek should return null");
        // Test adding values updates size and displays contained correctly
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.addLast(num);
            impl.addBack(num);
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.peekLast(), impl.peekBack(), "peeks should be the same");
            assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
        }
    }

    @Order(9)
    @DisplayName("Stress test for removeFront(...)")
    @ParameterizedTest(name = "Test removeFront()ing {1} random numbers with seed = {0}")
    @DependsOn({"constructor", "iterator", "size", "peekFront", "addFront", "removeFront", "toString"})
    @TestDescription("This test checks uses large amounts of random data to test that your adds and removes work correctly.")
    @CsvSource({
            "101, 300", "45, 500"
    })
    default void stressTestRemoveFront(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        // Test that first removeFront is null
        assertNull(impl.removeFront(), "empty removeFront should return null");
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.addFirst(num);
            impl.addFront(num);
            assertEquals(reference.peekFirst(), impl.peekFront(),"return values of peekFront()s are not equal");
            if (r.nextBoolean()) {
                assertEquals(reference.removeFirst(), impl.removeFront(),"return values of removeFront()s are not equal");
                assertEquals(reference.peekFirst(), impl.peekFront(),"return values of peekFront()s are not equal");
            }
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
        }
    }

    @Order(10)
    @DisplayName("Stress test for removeBack(...)")
    @ParameterizedTest(name = "Test removeBack()ing {1} random numbers with seed = {0}")
    @DependsOn({"constructor", "iterator", "size", "peekBack", "addBack", "removeBack", "toString"})
    @TestDescription("This test checks uses large amounts of random data to test that your adds and removes work correctly.")
    @CsvSource({
            "101, 300", "45, 500"
    })
    default void stressTestRemoveBack(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        // Test that first removeBack is null
        assertNull(impl.removeBack(), "empty removeBack should return null");
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            reference.addLast(num);
            impl.addBack(num);
            assertEquals(reference.peekLast(), impl.peekBack(),"return values of peekBack()s are not equal");
            if (r.nextBoolean()) {
                assertEquals(reference.removeLast(), impl.removeBack(),"return values of removeBack()s are not equal");
                assertEquals(reference.peekLast(), impl.peekBack(),"return values of peekBack()s are not equal");
            }
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
        }
    }

    @Order(11)
    @DisplayName("Stress test full IDeque")
    @ParameterizedTest(name = "Test all IDeque methods {1} random numbers with seed = {0}")
    @DependsOn({"everything"})
    @TestDescription("This test checks uses large amounts of random data to test that all your methods in the deque work correctly.")
    @CsvSource({
            "102, 300", "52, 500"
    })
    default void stressTestFullDeque(int seed, int size) {
        Random r = new Random(seed);
        Deque<Object> reference = new ArrayDeque<>();
        IDeque<Object> impl = newDeque();
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            // Add to either front or back
            if (r.nextBoolean()) {
                reference.addFirst(num);
                impl.addFront(num);
            }
            else {
                reference.addLast(num);
                impl.addBack(num);
            }
            // Test that peeks are correct
            assertEquals(reference.peekFirst(), impl.peekFront(),"return values of peekFront()s are not equal");
            assertEquals(reference.peekLast(), impl.peekBack(),"return values of peekBacks()s are not equal");
            // If true, remove an element
            if (r.nextBoolean()) {
                // If true, remove from front, else remove from back
                if (r.nextBoolean()) {
                    assertEquals(reference.removeFirst(), impl.removeFront(),"return values of removeFront()s are not equal");
                }
                else {
                    assertEquals(reference.removeLast(), impl.removeBack(),"return values of removeBack()s are not equal");
                }
                assertEquals(reference.peekFirst(), impl.peekFront(),"return values of peekFront()s are not equal");
                assertEquals(reference.peekLast(), impl.peekBack(),"return values of peekBacks()s are not equal");
            }
            assertEquals(reference.size(), impl.size(), "size()s are not equal");
            assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
        }
    }
}
