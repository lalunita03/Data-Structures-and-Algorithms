package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IStyleTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static edu.caltech.cs2.project05.Project05TestOrdering.classSpecificTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Tag("B")
@Order(1)
@ExtendWith(TestExtension.class)
public class ChainingHashDictionaryTests extends IDictionaryNGramTests {
    private static String DICTIONARY_SOURCE = "src/edu/caltech/cs2/datastructures/ChainingHashDictionary.java";

    public Counter supplierCounter = new Counter();

    @Override
    public int SINGLE_OP_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityWorst() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityBest() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @Override
    public IDictionary<Object, Object> newIDictionary() {
        Supplier<IDictionary<Object, Object>> sup = () -> {
            this.supplierCounter.touchCounter();
            return new MoveToFrontDictionary<>();
        };
        return new ChainingHashDictionary<>(sup);
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
            return ChainingHashDictionary.class;
        }

        @Override
        public List<String> getPublicInterface() {
            return List.of("containsKey", "containsValue", "get", "iterator", "keys", "put", "remove", "size", "values", "toString");
        }

        @Override
        public int getMaxFields() {
            return 5;
        }

        @Override
        public List<String> methodsToBanSelf() {
            return List.of("put", "get", "remove", "keys", "values");
        }
    }

    @DisplayName("Implementation")
    @Nested
    @Order(0)
    @Tag("C")
    class ImplementationTests {
        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive array allocation in put")
        @TestDescription("This test checks that you are not allocating extra arrays in put that are not necessary.")
        @DependsOn({"put"})
        @TestHint("You should be creating a new array only if you need to rehash to maintain the load factor")
        public void testForExcessiveArrayAllocationPut() {
            NewObjectArray.NUM_CALLS = 0;
            IDictionary<Object, Object> dict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

            for (int j = 0; j < 100; j++) {
                int before = NewObjectArray.NUM_CALLS;
                dict.put(j, j);
                int after = NewObjectArray.NUM_CALLS;
                assertTrue(before + 1 >= after, "Each put() should create at most one new array");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive array allocation in get")
        @Test
        @TestDescription("This test checks that you are not allocating any extra arrays in get that are not necessary.")
        @DependsOn({"put", "get"})
        @TestHint("get should not be affecting the dictionary in any way; it should simply find and return a value.")
        public void testForExcessiveArrayAllocationGet() {
            NewObjectArray.NUM_CALLS = 0;
            IDictionary<Object, Object> dict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

            for (int j = 0; j < 100; j++) {
                dict.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewObjectArray.NUM_CALLS;
                dict.get(j);
                int after = NewObjectArray.NUM_CALLS;
                assertEquals(after, before, "get() should not allocate any new array");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive array allocation in remove")
        @Test
        @TestDescription("This test checks that you are not allocating any extra arrays in get that are not necessary.")
        @DependsOn({"put", "remove"})
        public void testForExcessiveArrayAllocationRemove() {
            NewObjectArray.NUM_CALLS = 0;
            IDictionary<Object, Object> dict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

            for (int j = 0; j < 100; j++) {
                dict.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewObjectArray.NUM_CALLS;
                dict.remove(j);
                int after = NewObjectArray.NUM_CALLS;
                assertEquals(after, before, "remove() should not allocate any new array");
            }
        }


        @Order(specialTestLevel)
        @DisplayName("Test that resizing and initializing is fast")
        @Timeout(value = 20, unit = SECONDS)
        @Test
        @TestDescription("This test checks that your ChainingHashDictionary is able to rehash and create buckets quickly.")
        @TestHint("Make sure you are hard-coding primes into your structure and not regenerating the list at every rehash. Make sure resizing" +
                " is only done when the load factor is greater than 1. Lastly, go back to NGram and make sure you compare strings and not" +
                " characters.")
        @DependsOn({"put"})
        public void testGrowthCapability() {
            for (int i = 0; i < 25; i++) {
                IDictionary<Object, Object> dict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
                for (int j = 0; j < 500000; j++) {
                    dict.put(j, j);
                }
                // This _should_ get GC'd with a smaller heap size...
            }
        }

        @Order(specialTestLevel)
        @DisplayName("Test that hash map is resized appropriately")
        @Test
        @TestDescription("This test checks that you are not resizing the dictionary too much or too little.")
        @DependsOn({"put"})
        @TestHint("Make sure that you are resizing if and only if the load factor is greater than 1.")
        public void testResize() {
            List<Counter> values = new ArrayList<>();
            IDictionary<Object, Object> impl = newIDictionary();
            for (int i = 0; i < 100000; i++) {
                Counter c = new Counter(i);
                impl.put(c, i);
                // Insert newer counters at front so number of touches is ascending
                values.add(0, c);
            }

            // All counters should have been touched at least once
            int currMaxTouches = 1;
            for (Counter c : values) {
                assertTrue(c.touches >= currMaxTouches, "Key was not hashed enough times when rebuilding dictionary");
                currMaxTouches = Math.max(c.touches, currMaxTouches);
            }

            assertTrue(currMaxTouches > 10, "Dictionary was not resized enough times when building");
            // Compare to 2 - last insertion could have triggered resize (unlikely, but
            // might as well - important part
            // is ensuring an upper bound.
            assertTrue(values.get(0).touches <= 2, "Most recent key inserted was hashed too many times");
        }

        @Order(specialTestLevel)
        @DisplayName("Test that supplier function is used in resize")
        @Test
        @TestDescription("This test checks that you use the provided supplier function when resizing the dictionary.")
        @TestHint("Make sure you are initializing all the buckets of the resized dictionary appropriately with the supplier function. Do not use streams.")
        @DependsOn({"put"})
        public void testSupplierUsage() {
            // Reset from any previous tests
            supplierCounter.resetCounter();
            IDictionary<Object, Object> impl = newIDictionary();

            for (int i = 0; i < 100000; i++) {
                impl.put(i, i);
            }

            // Number of resized was arbitrarily chosen, but this *should* be fine?
            assertTrue(supplierCounter.touches > 50000, "Supplier was not used enough during resizes");
        }
    }

    private static class Counter {
        public int touches;
        public Object data;

        public Counter() {
            this(0);
        }

        public Counter(Object data) {
            this.touches = 0;
            this.data = data;
        }

        public void resetCounter() {
            this.touches = 0;
        }

        public void touchCounter() {
            this.touches++;
        }

        @Override
        public int hashCode() {
            this.touchCounter();
            return this.data.hashCode();
        }

        @Override
        // Equals does not count as a "touch"
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            } else if (!(o instanceof Counter)) {
                return false;
            }
            Counter c = (Counter) o;
            if (this.data == null || c.data == null) {
                return (this.data == null && c.data == null);
            }
            return this.data.equals(c.data);
        }

        @Override
        public String toString() {
            return this.data == null ? "null" : this.data.toString();
        }
    }
}
