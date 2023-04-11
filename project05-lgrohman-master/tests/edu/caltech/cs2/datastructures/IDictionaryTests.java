package edu.caltech.cs2.datastructures;

import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.caltech.cs2.helpers.DependsOn;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.helpers.TestExtension;
import edu.caltech.cs2.helpers.TestDescription;
import edu.caltech.cs2.helpers.TestHint;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static edu.caltech.cs2.project05.Project05TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

// This allows putting the burden of creating the reference map on the
// implementer, which enables control over key subclass / implementation.
// Nothing test-related is stored in instance variables, so this does not have (known) side effects.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestExtension.class)
public abstract class IDictionaryTests {
    abstract int SINGLE_OP_TIMEOUT_MS();

    abstract int CONTAINS_VALUE_TIMEOUT_MS();

    abstract RuntimeInstrumentation.ComplexityType getAndPutComplexity();
    abstract RuntimeInstrumentation.ComplexityType getAndPutComplexityWorst();
    abstract RuntimeInstrumentation.ComplexityType getAndPutComplexityBest();

    abstract IDictionary<Object, Object> newIDictionary();

    // This allows the subclass to require specific formats required for keys
    // e.g. IDictionary<K extends Comparable, V>, or IDictionary<K extends Iterable,
    // V>
    abstract Stream<Arguments> iDictionarySmokeDataSource();

    abstract Map<Object, Object> createReferenceMap(String[] keys, Object[] vals);

    abstract Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
            int maxKeyLength);

    public Map<Object, Object> generateRandomTestData(int size, Random rand) {
        return generateRandomTestData(size, rand, 10, 1, 20);
    }

    public void iDictionarySmokeTestHelper(Map<Object, Object> base, boolean testRemove) {
        IDictionary<Object, Object> impl = newIDictionary();
        Set<Object> seenValues = new HashSet<>();

        assertTrue(impl.isEmpty(), "Newly initialized IDictionary is nonempty");
        int expectedSize = 0;

        for (Object k : base.keySet()) {

            // Negative key + value tests
            assertFalse(impl.containsKey(k), "containsKey returns true for missing key " + k);
            if (!seenValues.contains(base.get(k))) {
                seenValues.add(base.get(k));
                assertFalse(impl.containsValue(base.get(k)), "containsValue returns true for missing value");
            }
            assertNull(impl.get(k), "Getting a missing key returns a non null value");

            // Put the key in
            assertNull(impl.put(k, ""), "Putting a new key " + k + " returns a non null value");
            expectedSize++;
            assertEquals(expectedSize, impl.size(), "Incorrect size");

            // Existing key tests
            assertEquals("", impl.get(k), "Getting an existing key " + k + " returns an incorrect value");
            assertEquals("", impl.put(k, base.get(k)), "Putting an existing key " + k + " returns an incorrect value");
            assertEquals(base.get(k), impl.get(k), "Getting an updated key " + k + " returns an old value");
            assertEquals(expectedSize, impl.size(), "Putting an existing key changed the size");
            assertTrue(impl.containsKey(k), "containsKey returns false for present key " + k);
            assertTrue(impl.containsValue(base.get(k)), "containsValue returns false for present value");
        }

        MatcherAssert.assertThat("keys", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.values().toArray()));

        if (testRemove) {
            Set<Object> keys = new HashSet<>(base.keySet());
            for (Object k : keys) {
                Object v = base.remove(k);
                assertEquals(v, impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");

                assertFalse(impl.containsKey(k), "containsKey returned true for removed key " + k);
                if (!base.containsValue(v)) {
                    assertFalse(impl.containsValue(v), "containsValue returned false for removed value");
                }
            }
        }
    }

    public void iDictionaryStressTestHelper(int seed, int size, boolean testRemove) {
        Random rand = new Random(seed);
        Map<Object, Object> excluded = new HashMap<>();
        Set<Object> includedKeys = new HashSet<>();
        List<Object> includedValues = new ArrayList<>();

        Map<Object, Object> base = generateRandomTestData(size, rand);
        IDictionary<Object, Object> impl = newIDictionary();

        // Randomly choose negative cases
        for (Map.Entry<Object, Object> e : base.entrySet()) {
            // Exclude this element
            if (rand.nextDouble() < 0.4) {
                excluded.put(e.getKey(), e.getValue());
            }
            // Include this element
            else {
                includedKeys.add(e.getKey());
                includedValues.add(e.getValue());
            }
        }

        // Build the dictionary, verify insertion and retrieval
        int expectedSize = 0;
        for (Map.Entry<Object, Object> e : base.entrySet()) {
            // If testRemove, excluded keys will be removed later
            if (testRemove || !excluded.containsKey(e.getKey())) {
                assertNull(impl.put(e.getKey(), ""), "Putting new key returns incorrect value");
                expectedSize++;
                assertEquals(expectedSize, impl.size(), "Adding new key did not appropriately change size");
                assertEquals("", impl.put(e.getKey(), e.getValue()), "Putting old key returns different value");
                assertEquals(e.getValue(), impl.get(e.getKey()), "Getting an updated key returns old value");
                assertEquals(expectedSize, impl.size(), "Putting existing key changed size");
            }
        }

        // Process removals if being tested
        if (testRemove) {
            for (Object k : excluded.keySet()) {
                assertEquals(base.remove(k), impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");
            }
        }
        // Match reference map state to what excluded keys
        else {
            for (Object k : excluded.keySet()) {
                base.remove(k);
            }
        }

        // Iterable checks
        MatcherAssert.assertThat("keys", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedValues.toArray()));

        // Positive key / value presence
        for (Object k : includedKeys) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertTrue(impl.containsKey(k), "Running containsKey on added key returns false.");
            });
            assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                assertTrue(impl.containsValue(base.get(k)), "Running containsValue on added value returns false.");
            });
        }

        // Negative key presence check
        for (Object k : excluded.keySet()) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertFalse(impl.containsKey(k), "Running containsKey on missing key returns true.");
            });

            Object v = excluded.get(k);
            if (!base.containsValue(v)) {
                assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                    assertFalse(impl.containsValue(v), "Running containsValue on missing value returns true.");
                });
            }
        }

    }

    @Order(1)
    @DisplayName("IDictionary Functionality")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IDictionaryTestsX {
        public Stream<Arguments> iDictionarySmokeDataSource() {
            return IDictionaryTests.this.iDictionarySmokeDataSource();
        }

        @Order(0)
        @DisplayName("Smoke test all IDictionary methods")
        @ParameterizedTest(name = "Test IDictionary interface on {0}")
        @MethodSource("iDictionarySmokeDataSource")
        @TestDescription("Tests functionality of all IDictionary methods, excluding remove")
        @TestHint("If you are failing this in BSTDictionary, make sure you pass the value you found up through your recursion and return it.")
        @DependsOn({"isEmpty", "get", "containsKey", "containsValue", "put", "size", "keys", "values", "iterator"})
        void smokeTestIDictionaryNoRemove(Map<Object, Object> base) {
            iDictionarySmokeTestHelper(base, false);
        }

        @Order(2)
        @DisplayName("Smoke test all IDictionary methods, with remove")
        @TestDescription("Tests functionality of all IDictionary methods, including remove")
        @TestHint("If you are failing this in BSTDictionary, make sure you pass the value you found up through your recursion and return it.")
        @DependsOn({"isEmpty", "get", "containsKey", "containsValue", "put", "size", "keys", "values", "iterator", "remove"})
        @ParameterizedTest(name = "Test IDictionary interface on {0}")
        @MethodSource("iDictionarySmokeDataSource")
        void smokeTestIDictionaryRemove(Map<Object, Object> base) {
            iDictionarySmokeTestHelper(base, true);
        }

        @Order(1)
        @DisplayName("Stress test all IDictionary methods")
        @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
        @CsvSource({"24589, 3000", "96206, 5000"})
        @TestDescription("Creates random data to test the stability of the IDictionary; does not test remove.")
        @DependsOn({"put", "size", "get", "keys", "iterator", "values", "containsKey", "containsValue"})
        void stressTestIDictionaryNoRemove(int seed, int size) {
            iDictionaryStressTestHelper(seed, size, false);
        }

        @Order(3)
        @DisplayName("Stress test all IDictionary methods, with remove")
        @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
        @CsvSource({"24589, 3000", "96206, 5000"})
        @TestDescription("Creates random data to test the stability of the IDictionary; DOES test remove.")
        @DependsOn({"put", "size", "get", "keys", "iterator", "values", "containsKey", "containsValue", "remove"})
        void stressTestIDictionaryRemove(int seed, int size) {
            iDictionaryStressTestHelper(seed, size, true);
        }
    }

    @DisplayName("Runtime Complexity (int keys)")
    @Nested
    class ComplexityTests {
        @Order(3)
        @DisplayName("Test get() complexity with int keys")
        @DependsOn({"put", "get"})
        @TestHint("If these tests are timing out in BSTDictionary, make sure you are not calling hashCode() in either your equals() or compareTo() in NGram." +
        " Also make sure that you are not comparing strings more than once and not calling equals() in your compareTo().")
        @Timeout(value = 20, unit = SECONDS)
        void testGetComplexityInt() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, i + 1);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> get = (Integer size, IDictionary<Object, Object> t) -> {
                t.get(5);
            };
            RuntimeInstrumentation.assertAtMost("get", getAndPutComplexityWorst(), provide, get, 8);
        }

        @Order(3)
        @DisplayName("Test containsKey() complexity with int keys")
        @DependsOn({"put", "containsKey"})
        @TestHint("If these tests are timing out in BSTDictionary, make sure you are not calling hashCode() in either your equals() or compareTo() in NGram." +
                " Also make sure that you are not comparing strings more than once and not calling equals() in your compareTo().")
        @Timeout(value = 20, unit = SECONDS)
        void testContainsKeyComplexityInt() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, i + 1);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> containsKey = (Integer size, IDictionary<Object, Object> t) -> {
                t.containsKey(5);
            };
            RuntimeInstrumentation.assertAtMost("containsKey", getAndPutComplexityWorst(), provide, containsKey, 8);
        }

        @Order(specialTestLevel)
        @DisplayName("Test put() complexity with int keys")
        @Test
        @DependsOn({"put"})
        @TestHint("If these tests are timing out in BSTDictionary, make sure you are not calling hashCode() in either your equals() or compareTo() in NGram." +
                " Also make sure that you are not comparing strings more than once and not calling equals() in your compareTo().")
        @Timeout(value = 20, unit = SECONDS)
        void testPutComplexityInt() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, i + 1);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> put = (Integer size, IDictionary<Object, Object> t) -> {
                t.put(size - 5, -1);
            };
            RuntimeInstrumentation.assertAtMost("put", getAndPutComplexityWorst(), provide, put, 8);
        }

        @Order(specialTestLevel)
        @DisplayName("Test remove() complexity with int keys")
        @Test
        @TestHint("If these tests are timing out in BSTDictionary, make sure you are not calling hashCode() in either your equals() or compareTo() in NGram." +
                " Also make sure that you are not comparing strings more than once and not calling equals() in your compareTo().")
        @DependsOn({"put", "remove"})
        @Timeout(value = 20, unit = SECONDS)
        void testRemoveComplexityInt() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, i + 1);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> remove = (Integer size, IDictionary<Object, Object> t) -> {
                t.remove(size - 5);
            };
            RuntimeInstrumentation.assertAtMost("remove", getAndPutComplexityWorst(), provide, remove, 8);
        }

        @Order(specialTestLevel)
        @DisplayName("Test size() complexity with int keys")
        @Test
        @TestHint("If these tests are timing out in BSTDictionary, make sure you are not calling hashCode() in either your equals() or compareTo() in NGram." +
                " Also make sure that you are not comparing strings more than once and not calling equals() in your compareTo().")
        @DependsOn({"put", "size"})
        @Timeout(value = 20, unit = SECONDS)
        void testSizeComplexityInt() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(new NGram(new String[]{"" + i}), 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> sizez = (Integer size, IDictionary<Object, Object> t) -> {
                t.size();
            };
            RuntimeInstrumentation.assertAtMost("size", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, sizez, 8);
        }
    }
}
