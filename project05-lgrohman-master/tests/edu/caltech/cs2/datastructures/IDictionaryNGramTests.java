package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.DependsOn;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.helpers.TestDescription;
import edu.caltech.cs2.helpers.TestExtension;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project05.Project05TestOrdering.sanityTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Wrapper class for all IDictionaries that will be tested using NGram keys to reduce code repetition
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
public abstract class IDictionaryNGramTests extends IDictionaryTests {

    @Override
    public Stream<Arguments> iDictionarySmokeDataSource() {
        return Stream.of(
                Arguments.of(createReferenceMap(new String[] { "a", "ab", "abc", "abcd", "abcde" },
                        new Integer[] { 1, 2, 3, 4, 5 })),
                Arguments.of(createReferenceMap(new String[] { "abcde", "abcd", "abc", "ab", "a" },
                        new Integer[] { 1, 2, 3, 4, 5 })),
                Arguments.of(createReferenceMap(new String[] { "a", "add", "app" },
                        new String[] { "hello", "1 + 1", "for a phone" })),
                Arguments.of(createReferenceMap(
                        new String[] { "adam", "add", "app", "bad", "bag", "bags", "beds", "bee", "cab" },
                        new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 })));
    }

    @Override
    public Map<Object, Object> createReferenceMap(String[] keys, Object[] vals) {
        Map<Object, Object> ref = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            ref.put(NGramTests.stringToNGram(keys[i]), vals[i]);
        }
        return ref;
    }

    @Override
    public Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
                                                       int maxKeyLength) {
        Map<Object, Object> base = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int keyLength = minKeyLength + rand.nextInt(maxKeyLength - minKeyLength);
            String[] key = new String[keyLength];
            for (int j = 0; j < keyLength; j++) {
                key[j] = String.valueOf(rand.nextInt(maxNodeDegree));
            }
            base.put(new NGram(key), rand.nextInt());
        }
        return base;
    }

    @Order(sanityTestLevel)
    @DisplayName("Test that keys are not compared using reference equality")
    @TestDescription("This test checks if you are comparing accidentally comparing the keys using == instead of .equals")
    @DependsOn({"equals"})
    public void testNoReferenceEquality() {
        IDictionary<Object, Object> t = newIDictionary();
        for (int i = 0; i < 10; i++) {
            t.put(new NGram(new String[] {"" + i}), 0);
        }
        for (int i = 0; i < 10; i ++) {
            assertTrue(t.containsKey(new NGram(new String[] {new String("" + i)})),
                    "NGram that is a distinct object with same data should be a contained key, but is not");
        }
    }

    @DisplayName("Runtime Complexity (NGram keys)")
    @Nested
    class ComplexityTestsNGram {
        @Order(specialTestLevel)
        @DisplayName("Test get() complexity with NGram keys")
        @DependsOn({"put", "get"})
        @Test
        @Timeout(value = 20, unit = SECONDS)
        public void testGetComplexity() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(new NGram(new String[]{"" + i}), 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> get = (Integer size, IDictionary<Object, Object> t) -> {
                t.get(new NGram(new String[]{"0"}));
            };
            RuntimeInstrumentation.assertAtMost("get", getAndPutComplexity(), provide, get, 10);
        }

        @Order(specialTestLevel)
        @DisplayName("Test put() complexity with NGram keys")
        @DependsOn({"put"})
        @Test
        @Timeout(value = 20, unit = SECONDS)
        public void testPutComplexity() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(new NGram(new String[]{"" + i}), 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> put = (Integer size, IDictionary<Object, Object> t) -> {
                t.put(new NGram(new String[]{"0"}), 0);
            };
            RuntimeInstrumentation.assertAtMost("put", getAndPutComplexity(), provide, put, 10);
        }

        @Order(specialTestLevel)
        @DisplayName("Test remove() complexity with NGram keys")
        @DependsOn({"put", "remove"})
        @Test
        @Timeout(value = 20, unit = SECONDS)
        public void testRemoveComplexity() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(new NGram(new String[]{"" + i}), 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> remove = (Integer size, IDictionary<Object, Object> t) -> {
                t.remove(new NGram(new String[]{"0"}));
            };
            RuntimeInstrumentation.assertAtMost("remove", getAndPutComplexity(), provide, remove, 10);
        }

        @Order(specialTestLevel)
        @DisplayName("Test size() complexity with NGram keys")
        @DependsOn({"put", "size"})
        @Timeout(value = 20, unit = SECONDS)
        public void testSizeComplexity() {
            Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
                IDictionary<Object, Object> t = newIDictionary();
                for (int i = 0; i < numElements - 4; i++) {
                    t.put(new NGram(new String[]{"" + i}), 0);
                }
                return t;
            };

            BiConsumer<Integer, IDictionary<Object, Object>> size = (Integer s, IDictionary<Object, Object> t) -> {
                t.size();
            };
            RuntimeInstrumentation.assertAtMost("size", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, size, 8);
        }
    }
}
