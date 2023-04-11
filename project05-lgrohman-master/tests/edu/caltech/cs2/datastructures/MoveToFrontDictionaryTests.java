package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IStyleTests;
import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static edu.caltech.cs2.project05.Project05TestOrdering.classSpecificTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(0)
@Tag("B")
@ExtendWith(TestExtension.class)
public class MoveToFrontDictionaryTests extends IDictionaryNGramTests {
    private static String DICTIONARY_SOURCE = "src/edu/caltech/cs2/datastructures/MoveToFrontDictionary.java";
    public int CONSTANT_TIMEOUT_MS = 50; // was 15, have heard reports of ~40.

    public int SINGLE_OP_TIMEOUT_MS() {
        return 100;
    }

    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LINEAR;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityWorst() {
        return RuntimeInstrumentation.ComplexityType.LINEAR;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityBest() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @DisplayName("Style")
    @Nested
    @Order(100)
    @Tag("C")
    class StyleTests implements IStyleTests {
        @Override
        public String getSource() {
            return DICTIONARY_SOURCE;
        }

        @Override
        public Class<?> getClazz() {
            return MoveToFrontDictionary.class;
        }

        @Override
        public List<String> getPublicInterface() {
            return List.of("containsKey", "containsValue", "get", "iterator", "keys", "put", "remove", "size", "values", "toString");
        }

        @Override
        public int getMaxFields() {
            return 3;
        }

        @Override
        public List<String> methodsToBanSelf() {
            return List.of("put", "get", "remove", "keys", "values");
        }

    }

    @DisplayName("Runtime Complexity (best case, int keys)")
    @Nested
    class RuntimeTestsBest {
        @Order(specialTestLevel)
        @DisplayName("Test get() best case complexity with int keys")
        @DependsOn({"put", "get"})
        @Test
        @Timeout(value = 20, unit = SECONDS)
        public void testBestCase() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> get = (Integer size, IDictionary<Object, Object> t) -> {
                t.get(size - 5);
            };
            RuntimeInstrumentation.assertAtMost("get", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, get, 8);
        }

        @Order(specialTestLevel)
        @DisplayName("Test put() best case complexity with int keys")
        @DependsOn({"put"})
        @Test
        @Timeout(value = 20, unit = SECONDS)
        public void testBestCasePut() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(i, 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> put = (Integer size, IDictionary<Object, Object> t) -> {
                t.put(size - 5, 0);
            };
            RuntimeInstrumentation.assertAtMost("put", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, put, 8);
        }
    }

    // No restrictions on key type for MoveToFront
    @Override
    public IDictionary<Object, Object> newIDictionary() {
        return new MoveToFrontDictionary<>();
    }

    @DisplayName("Implementation")
    @Order(0)
    @Nested
    class ImplementationTests {
        @Order(classSpecificTestLevel)
        @DisplayName("Check MoveToFrontDictionary class is properly implemented")
        @TestDescription("This test makes sure that the implementation of the MoveToFront dictionary, i.e. like having the right fields")
        @TestHint("MoveToFrontDictionary should only have a head node and an int field to store size. It should also implement a Node class.")
        @DependsOn({"fields", "node class"})
        @Test
        public void checkMTF() {
            MoveToFrontChecker.checkClass(MoveToFrontDictionary.class);
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive node allocation in put")
        @TestDescription("This test checks that no extra nodes are allocated in the put() method")
        @DependsOn({"put"})
        @Test
        public void testForExcessiveNodeAllocationPut() {
            NewNode.MoveToFrontDictionary_NUM_CALLS = 0;
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();

            for (int j = 0; j < 100; j++) {
                int before = NewNode.MoveToFrontDictionary_NUM_CALLS;
                impl.put(j, j);
                int after = NewNode.MoveToFrontDictionary_NUM_CALLS;
                assertTrue(before + 1 >= after, "Each put() should create at most one new node");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive node allocation in get")
        @TestDescription("This test checks that no extra nodes are allocated in the get() method")
        @DependsOn({"put", "get"})
        @TestHint("get should not be affecting the dictionary in any way; it should simply find and return a value.")
        @Test
        public void testForExcessiveNodeAllocationGet() {
            NewNode.MoveToFrontDictionary_NUM_CALLS = 0;
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();

            for (int j = 0; j < 100; j++) {
                impl.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewNode.MoveToFrontDictionary_NUM_CALLS;
                impl.get(j);
                int after = NewNode.MoveToFrontDictionary_NUM_CALLS;
                assertEquals(before, after, "get() should not allocate any new node");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive Node allocation in remove")
        @TestDescription("This test checks that no extra nodes are allocated in the remove() method")
        @DependsOn({"put", "remove"})
        @Test
        public void testForExcessiveNodeAllocationRemove() {
            NewObjectArray.NUM_CALLS = 0;
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();

            for (int j = 0; j < 100; j++) {
                impl.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewNode.MoveToFrontDictionary_NUM_CALLS;
                impl.remove(j);
                int after = NewNode.MoveToFrontDictionary_NUM_CALLS;
                assertEquals(before, after, "remove() should not allocate any new node");
            }
        }


        @Test
        @DisplayName("Sanity check that accessing keys in various locations in the dictionary works")
        @TestDescription("This test tries to obtain and remove data from various parts of the dictionary (front, back) to make sure its in the data structure")
        @DependsOn({"put", "get", "remove"})
        @Order(classSpecificTestLevel)
        public void testDataLocations() {
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
            HashMap<Integer, Integer> ref = new HashMap<>();

            for (int i = 0; i < 10; i++) {
                impl.put(i, i);
                ref.put(i, i);
            }

            // Check access of element at front
            assertEquals(ref.get(9), impl.get(9), "Getting an element from the front failed.");

            // Check accessing whatever element is at the back
            for (int i = 0; i < 10; i++) {
                assertEquals(ref.get(i), impl.get(i), "Getting an element from the back returns incorrect result.");
                assertEquals(ref.get(i), impl.get(i), "Key that was just gotten is now missing.");
            }

            // Check removing element at the front
            for (int i = 9; i >= 0; i--) {
                assertEquals(ref.remove(i), impl.remove(i), "Removing an element from the front failed.");
            }

            // Repopulate to make sure that emptying it didn't bork it
            for (int i = 0; i < 10; i++) {
                impl.put(i, i);
                ref.put(i, i);
            }

            // And repeat.
            assertEquals(ref.get(9), impl.get(9));
            for (int i = 0; i < 10; i++) {
                assertEquals(ref.get(i), impl.get(i), "Getting an element from the back failed.");
            }
        }

        @Test
        @DisplayName("Test that referencing a key moves it to the front")
        @TestDescription("This test is checking the fundamental principle of the MoveToFrontDictionary - that when you access a key, it does get moved to the front")
        @TestHint("Make sure to move to front in both containsKey and get")
        @DependsOn({"put", "containsKey", "get"})
        @Order(specialTestLevel)
        public void testMoveToFrontProperty() {
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
            final int DICT_SIZE = 30000;
            for (int i = 0; i <= DICT_SIZE; i++) {
                impl.put(i, i);
            }

            double totalTimeRetrieveFront = 0;
            long startTime, endTime;
            for (int i = 0; i <= DICT_SIZE; i++) {
                // Get key from back to move to front
                impl.containsKey(i);

                // Clock retrieval of key from front.
                startTime = System.nanoTime();
                impl.get(i);
                endTime = System.nanoTime();
                totalTimeRetrieveFront += (endTime - startTime);
            }
            assertTrue(CONSTANT_TIMEOUT_MS > totalTimeRetrieveFront / 1000000,
                    "get(key) after calling containsKey(key) takes too long.");

            totalTimeRetrieveFront = 0;
            for (int i = 0; i <= DICT_SIZE; i++) {
                // Get key from back to move to front
                impl.get(i);

                // Clock retrieval of key from front.
                startTime = System.nanoTime();
                impl.get(i);
                endTime = System.nanoTime();
                totalTimeRetrieveFront += (endTime - startTime);
            }
            assertTrue(CONSTANT_TIMEOUT_MS > totalTimeRetrieveFront / 1000000,
                    "get(key) after calling get(key) takes too long.");
        }

        @Order(specialTestLevel)
        @DisplayName("Test removing from the front has the desired behavior")
        @TestDescription("This test makes sure that removing from the front updates the head and the rest of the linked list correctly")
        @TestHint("Make sure you update your head field correctly in removal, and that if a key is removed, the MoveToFrontDictionary is able to recognize and handle that")
        @DependsOn({"put", "get", "remove", "values"})
        @Test
        public void testFrontRemove() {
            MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
            final int DICT_SIZE = 1000;
            for (int i = 0; i <= DICT_SIZE; i++) {
                impl.put(i, i);
            }
            Class nodeClass = MoveToFrontChecker.getNodeClass(MoveToFrontDictionary.class);
            Field head = null;
            for (Field field : MoveToFrontDictionary.class.getDeclaredFields()) {
                if (field.getType().equals(nodeClass)) {
                    field.setAccessible(true);
                    head = field;
                    break;
                }
            }
            assertNotNull(head, "There is no head field in MoveToFrontDictionary");
            for (int i = DICT_SIZE; i >= 0; i--) {
                assertEquals(i, impl.get(i), "Getting a key does not return the correct value");
                assertEquals(i, impl.remove(i), "Removing a key does not return the correct value");
                try {
                    if (i != 0) {
                        assertNotNull(head.get(impl), "Removing from front leaves a null head pointer");
                    } else {
                        assertNull(head.get(impl), "Removing last key does not set head pointer to null");
                    }
                    ICollection<Integer> values = impl.values();
                    assertNull(impl.get(i), "Getting a removed key does not return null");
                    assertIterableEquals(values, impl.values(), "Getting a removed key changes the value list");
                    if (i != 0) {
                        assertNotNull(head.get(impl), "Getting a removed key leaves a null head pointer");
                    } else {
                        assertNull(head.get(impl), "Getting the last key does leave a null head pointer");
                    }
                } catch (IllegalAccessException ex) {
                    fail("There is no head field in MoveToFrontDictionary");
                }
            }
        }
    }
}
