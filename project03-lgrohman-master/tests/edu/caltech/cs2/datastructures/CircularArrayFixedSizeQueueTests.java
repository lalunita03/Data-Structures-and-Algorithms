package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Tag("B")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
public class CircularArrayFixedSizeQueueTests {
  private static String FIXED_QUEUE_SOURCE = "src/edu/caltech/cs2/datastructures/CircularArrayFixedSizeQueue.java";

  private Constructor circFixedSizeQueueConstructor = Reflection.getConstructor(CircularArrayFixedSizeQueue.class,
          int.class);

  public IFixedSizeQueue<Object> newFixedSizeQueue(int capacity) {
    return Reflection.newInstance(circFixedSizeQueueConstructor, capacity);
  }

  @DisplayName("Style")
  @Nested
  class StyleTests implements IStyleTests {
    public int getMaxFields() {
      return 4;
    }

    @Override
    public List<String> methodsToBanSelf() {
      return List.of("dequeue", "iterator");
    }

    public List<String> getPublicInterface() {
      return List.of("enqueue", "dequeue", "peek", "iterator", "size", "isFull", "capacity", "toString", "add", "clear");
    }

    @Override
    public String getSource() {
      return FIXED_QUEUE_SOURCE;
    }

    @Override
    public Class<?> getClazz() {
      return CircularArrayFixedSizeQueue.class;
    }
  }

  @Nested
  @DisplayName("Runtime Complexity")
  class RuntimeTests {
    @Order(complexityTestLevel)
    @DisplayName("enqueue() and dequeue() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test()
    public void testQueueOperationComplexity() {
      Function<Integer, IFixedSizeQueue<Integer>> provide = (Integer numElements) -> {
        Constructor c = Reflection.getConstructor(CircularArrayFixedSizeQueue.class, int.class);
        IFixedSizeQueue<Integer> q = Reflection.newInstance(c, numElements * 2);
        for (int i = 0; i < numElements; i++) {
          q.enqueue(i);
        }
        return q;
      };
      Consumer<IFixedSizeQueue<Integer>> enqueue = (IFixedSizeQueue<Integer> q) -> q.enqueue(0);
      Consumer<IFixedSizeQueue<Integer>> dequeue = (IFixedSizeQueue<Integer> q) -> q.dequeue();

      RuntimeInstrumentation.assertAtMost("enqueue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, enqueue, 8);
      RuntimeInstrumentation.assertAtMost("dequeue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, dequeue, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("peek() takes constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test()
    public void testPeekComplexity() {
      Function<Integer, IFixedSizeQueue<Integer>> provide = (Integer numElements) -> {
        Constructor c = Reflection.getConstructor(CircularArrayFixedSizeQueue.class, int.class);
        IFixedSizeQueue<Integer> q = Reflection.newInstance(c, numElements * 2);
        for (int i = 0; i < numElements; i++) {
          q.enqueue(i);
        }
        return q;
      };
      Consumer<IFixedSizeQueue<Integer>> peek = (IFixedSizeQueue<Integer> q) -> q.peek();

      RuntimeInstrumentation.assertAtMost("peek", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peek, 8);
    }
  }

  @Nested
  @DisplayName("IFixedSizeQueue Functionality")
  class QueueTests implements IFixedSizeQueueTests {

    public IFixedSizeQueue<Object> newQueue() {
      return Reflection.newInstance(circFixedSizeQueueConstructor);
    }

    public IFixedSizeQueue<Object> newQueue(int size) {
      return Reflection.newInstance(circFixedSizeQueueConstructor, size);
    }

    @Override
    public IFixedSizeQueue<Object> newFixedSizeQueue(int capacity) {
      return CircularArrayFixedSizeQueueTests.this.newFixedSizeQueue(capacity);
    }
  }

  @Nested
  @DisplayName("ICollection Functionality")
  class CollectionTests implements ICollectionTests {
    @Override
    public ICollection<Object> newCollection() {
      return Reflection.newInstance(circFixedSizeQueueConstructor, 100000);
    }

    @Override
    @Order(collectionTestLevel)
    @DisplayName("Simple tests of various ICollection functions")
    @ParameterizedTest(name = "Test add(), size(), isEmpty(), contains(), and clear() on [{arguments}]")
    @DependsOn({"constructor", "add", "clear", "size", "iterator"})
    @TestDescription("This test checks the built-in functions we gave you by using your iterator and size methods.")
    @ValueSource(strings = {
            "",
            "1",
            "0, 1, 2, 3",
            "5, 4, 3, 2, 1",
            "8, 3, 5, 7, 4, 3, 12, 12, 1"
    })
    public void testCollectionFunctions(String inputs) {
      ICollectionTests.super.testCollectionFunctions(inputs);
    }

    @Override
    @Order(collectionTestLevel)
    @Test
    @DisplayName("Test repeated emptying and filling of ICollection with single element")
    @DependsOn({"constructor", "add", "clear", "size"})
    @TestDescription("This test repeatedly adds and clears the data structure.")
    public void testFillEmptyCollection() {
      ICollectionTests.super.testFillEmptyCollection();
    }

    @Override
    @Order(collectionTestLevel)
    @DisplayName("Stress test for add(...)")
    @ParameterizedTest(name = "Test add()ing {1} random numbers with seed = {0}")
    @DependsOn({"constructor", "size", "add", "iterator"})
    @TestDescription("This test adds a ton of random numbers to your data structure and checks that they're all actually in there.")
    @CsvSource({
            "100, 3000", "42, 1000"
    })
    public void stressTestAdd(int seed, int size) {
      ICollectionTests.super.stressTestAdd(seed, size);
    }

    @Override
    @Order(collectionTestLevel)
    @DisplayName("Stress test for contains(...)")
    @ParameterizedTest(name = "Test contains() with {1} random numbers and seed = {0}")
    @DependsOn({"constructor", "add", "iterator"})
    @TestDescription("This test adds a ton of random numbers to your data structure and checks that they're all actually in there.")
    @CsvSource({
            "100, 3000", "42, 1000"
    })
    public void stressTestContains(int seed, int size) {
      ICollectionTests.super.stressTestContains(seed, size);
    }
  }

  @DisplayName("Implementation Tests")
  @Nested
  class ImplementationTests {
    @Order(fixedSizeQueueLevel)
    @DisplayName("Test iterator matches reference for wraparound values")
    @TestHint("This test is likely failing because you don't have correct wrap-around behavior.\n     Remember that everything should advance; so, it's possible that your start wraps around.")
    @ParameterizedTest(name = "Test iterator and wraparound behavior with {1} random values with seed = {0} and fixed array size = {2}")
    @CsvSource({"69, 200, 20", "21, 300, 200"})
    public void testWrapAround(int seed, int numVals, int queueSize) {
      Random r = new Random(seed);
      IFixedSizeQueue<Object> me = newFixedSizeQueue(queueSize);
      Queue<Object> reference = new java.util.ArrayDeque<>();
      assertEquals(queueSize, me.capacity(), "capacity does not match expected value");
      for (int i = 0; i < queueSize; i++) {
        int num = r.nextInt();
        assertFalse(me.isFull(), "queue should not be full");
        assertTrue(me.enqueue(num), "enqueue should be successful");
        reference.add(num);

      }
      for (int i = 0; i < numVals; i++) {
        me.enqueue(me.dequeue());
        reference.add(reference.remove());
        assertEquals(reference.peek(), me.peek(), "return values of peek()s are not equal");
        assertEquals(reference.size(), me.size(), "size()s are not equal");
        assertEquals(queueSize, me.capacity(), "capacity of a fixed size queue should not change");
        assertIterableEquals(reference, me, "Reference and implemented queues are not equal");
      }
    }

    @Order(toStringTestLevel)
    @DisplayName("toString() matches java.util.ArrayDeque")
    @DependsOn({"fields", "constructors", "toString", "add"})
    @ParameterizedTest(name = "Test toString() on [{arguments}]")
    @ValueSource(strings = {
            "0, 1, 2, 3", "5, 4, 3, 2, 1", "8, 3, 5, 7, 4, 3, 12, 12, 1"
    })
    public void testToString(String inputs) {
      java.util.ArrayDeque<String> reference = new java.util.ArrayDeque<String>();
      edu.caltech.cs2.interfaces.IFixedSizeQueue<Object> me = new CircularArrayFixedSizeQueue<>(10);
      for (String value : inputs.trim().split(", ")) {
        assertEquals(reference.toString(), me.toString(), "toString outputs should be the same");
        reference.addLast(value);
        me.enqueue(value);
      }
    }
  }
}