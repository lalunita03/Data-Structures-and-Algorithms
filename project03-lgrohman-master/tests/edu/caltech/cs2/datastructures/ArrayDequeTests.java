package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("C")
@ExtendWith(TestExtension.class)
public class ArrayDequeTests {
  private static String ARRAY_DEQUE_SOURCE = "src/edu/caltech/cs2/datastructures/ArrayDeque.java";

  private Constructor arrayDequeConstructor = Reflection.getConstructor(ArrayDeque.class);

  // ARRAYDEQUE-SPECIFIC TESTS ----------------------------------------

  @DisplayName("Style")
  @Nested
  class StyleTests implements IStyleTests {
    @Order(classSpecificTestLevel)
    @DisplayName("There is an integer default capacity static field and an integer default grow factor static field")
    @TestDescription("This test checks that you use constants (static, final, private) fields for the default capacity and the growth factor when the array resizes.")
    @Test
    public void testConstantFields() {
      Reflection.assertFieldsEqualTo(ArrayDeque.class, "static", 2);
      Stream<Field> fields = Reflection.getFields(ArrayDeque.class);
      fields.filter(Reflection.hasModifier("static")).forEach((field) -> {
        Reflection.checkFieldModifiers(field, List.of("private", "static", "final"));
        assertEquals(int.class, field.getType(), "static fields must be of type int");
      });
    }

    public int getMaxFields() {
      return 5;
    }

    @Override
    public List<String> methodsToBanSelf() {
      return List.of("addFront", "addBack", "removeBack", "removeFront", "iterator");
    }

    public List<String> getPublicInterface() {
      return List.of(
              "addFront",
              "addBack",
              "removeFront",
              "removeBack",
              "enqueue",
              "dequeue",
              "push",
              "pop",
              "peek",
              "peekFront",
              "peekBack",
              "iterator",
              "size",
              "toString"
      );
    }

    @Override
    public String getSource() {
      return ARRAY_DEQUE_SOURCE;
    }

    @Override
    public Class<?> getClazz() {
      return ArrayDeque.class;
    }
  }

  // TIME COMPLEXITY TESTS ------------------------------------------------

  @Nested
  @DisplayName("Runtime Complexity")
  class RuntimeTests {
    @Order(complexityTestLevel)
    @DisplayName("addFront() and removeFront() take linear time")
    @Timeout(value = 20, unit = SECONDS)
    @Test()
    public void testFrontDequeOperationComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addFront(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> addFront = (IDeque<Integer> q) -> q.addFront(0);
      Consumer<IDeque<Integer>> removeFront = (IDeque<Integer> q) -> q.removeFront();

      RuntimeInstrumentation.assertAtMost("addFront", RuntimeInstrumentation.ComplexityType.LINEAR, provide, addFront, 8);
      RuntimeInstrumentation.assertAtMost("removeFront", RuntimeInstrumentation.ComplexityType.LINEAR, provide, removeFront, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("addBack() and removeBack() take constant time")
    @Timeout(value = 20, unit = SECONDS)
    @Test
    public void testBackDequeOperationComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addBack(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> addBack = (IDeque<Integer> q) -> q.addBack(0);
      Consumer<IDeque<Integer>> removeBack = (IDeque<Integer> q) -> q.removeBack();

      RuntimeInstrumentation.assertAtMost("addBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addBack, 8);
      RuntimeInstrumentation.assertAtMost("removeBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, removeBack, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("enqueue() and dequeue() take linear time")
    @Timeout(value = 20, unit = SECONDS)
    @Test
    public void testQueueOperationComplexity() {
      Function<Integer, IQueue<Integer>> provide = (Integer numElements) -> {
        IQueue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.enqueue(i);
        }
        return q;
      };
      Consumer<IQueue<Integer>> enqueue = (IQueue<Integer> q) -> q.enqueue(0);
      Consumer<IQueue<Integer>> dequeue = (IQueue<Integer> q) -> q.dequeue();

      RuntimeInstrumentation.assertAtMost("enqueue", RuntimeInstrumentation.ComplexityType.LINEAR, provide, enqueue, 8);
      RuntimeInstrumentation.assertAtMost("dequeue", RuntimeInstrumentation.ComplexityType.LINEAR, provide, dequeue, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("push() and pop() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testStackOperationComplexity() {
      Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
        IStack<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.push(i);
        }
        return q;
      };
      Consumer<IStack<Integer>> push = (IStack<Integer> q) -> q.push(0);
      Consumer<IStack<Integer>> pop = (IStack<Integer> q) -> q.pop();

      RuntimeInstrumentation.assertAtMost("push", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, push, 8);
      RuntimeInstrumentation.assertAtMost("pop", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, pop, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("peek() takes constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testPeekComplexity() {
      Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
        IStack<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.push(i);
        }
        return q;
      };
      Consumer<IStack<Integer>> peek = (IStack<Integer> q) -> q.peek();

      RuntimeInstrumentation.assertAtMost("peek", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peek, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("peekFront() takes constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test()
    public void testPeekFrontComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addFront(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> peekFront = (IDeque<Integer> q) -> q.peekFront();

      RuntimeInstrumentation.assertAtMost("peekFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekFront, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("peekBack() takes constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testPeekBackComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addBack(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> peekBack = (IDeque<Integer> q) -> q.peekBack();

      RuntimeInstrumentation.assertAtMost("peekBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekBack, 8);
    }
  }

  @Nested
  @DisplayName("IStack Functionality")
  class StackTests implements IStackTests {
    @Override
    public IStack<Object> newStack() {
      return Reflection.newInstance(arrayDequeConstructor);
    }
  }

  @Nested
  @DisplayName("IQueue Functionality")
  class QueueTests implements IQueueTests {

    public IQueue<Object> newQueue() {
      return Reflection.newInstance(arrayDequeConstructor);
    }

    public IQueue<Object> newQueue(int size) {
      return newQueue();
    }
  }

  @Nested
  @DisplayName("IDeque Functionality")
  class DequeTests implements IDequeTests {
    @Override
    public IDeque<Object> newDeque() {
      return Reflection.newInstance(arrayDequeConstructor);
    }
  }

  @Nested
  @DisplayName("ICollection Functionality")
  class CollectionTests implements ICollectionTests {
    @Override
    public ICollection<Object> newCollection() {
      return Reflection.newInstance(arrayDequeConstructor);
    }
  }

  @DisplayName("Implementation Tests")
  @Nested
  class ImplementationTests {
    @Order(implSpecificTestLevel)
    @DisplayName("The default capacity of the array in the deque is 10")
    @DependsOn({"fields", "constructors"})
    @Test
    public void testArrayDequeDefaultInitialCapacity() throws IllegalAccessException {
      ArrayDeque<Integer> impl = new ArrayDeque<>();

      // Reflect and get the backing array
      // It's actually an Object[] since that's how it (should!) be initialized internally
      // Casting it doesn't change the type of the field.
      // It's fine since there should only be one array.
      Field arr = Reflection.getFieldByType(ArrayDeque.class, Object[].class);
      arr.setAccessible(true);
      Object[] backingArray = (Object[]) arr.get(impl);

      assertEquals(10, backingArray.length, "Default initial capacity is not 10");
    }

    @Order(implSpecificTestLevel)
    @DisplayName("enqueue should always succeed")
    @TestDescription("This test is checking every enqueue in your ArrayDeque returns true.\nThe only time an enqueue might return false is if it doesn't resize (like in the B Tests).")
    @DependsOn({"fields", "constructors", "enqueue"})
    @Test
    public void testThatArrayDequeEnqueueAlwaysSucceeds() {
      ArrayDeque<Integer> impl = new ArrayDeque<>();
      for (int i = 0; i < 100; i++) {
        assertTrue(impl.enqueue(i), "enqueue() should always succeed for ArrayDeque");
      }
    }

    @Order(implSpecificTestLevel)
    @DisplayName("Check for excessive array allocation in addFront and removeFront")
    @TestDescription("This test is checking that you are not allocating extra arrays in add/remove that are not necessary.")
    @DependsOn({"fields", "constructors", "addFront", "removeFront"})
    @Test
    public void testForExcessiveArrayAllocationAddFront() {
      NewObjectArray.NUM_CALLS = 0;
      ArrayDeque<Integer> impl = new ArrayDeque<>();
      for (int i = 0; i < 100; i++) {
        int before = NewObjectArray.NUM_CALLS;
        impl.addFront(i);
        int after = NewObjectArray.NUM_CALLS;
        assertTrue(before + 1 >= after, "Each addFront() should create at most one new array");
      }
      assertTrue(NewObjectArray.NUM_CALLS < 2 * ((int) (Math.log(100) / Math.log(2))), "addFront() should not use an excessive number of new arrays");
      for (int i = 0; i < 100; i++) {
        int before = NewObjectArray.NUM_CALLS;
        impl.removeFront();
        int after = NewObjectArray.NUM_CALLS;
        assertTrue(before == after, "removeFront() should not allocate any new arrays");
      }
    }

    @Order(implSpecificTestLevel)
    @DisplayName("Check for excessive array allocation in addBack and removeBack")
    @TestDescription("This test is checking that you are not allocating extra arrays in add/remove that are not necessary.")
    @DependsOn({"fields", "constructors", "addBack", "removeBack"})
    @Test
    public void testForExcessiveArrayAllocationAddBack() {
      NewObjectArray.NUM_CALLS = 0;
      ArrayDeque<Integer> impl = new ArrayDeque<>();
      for (int i = 0; i < 100; i++) {
        int before = NewObjectArray.NUM_CALLS;
        impl.addBack(i);
        int after = NewObjectArray.NUM_CALLS;
        assertTrue(before + 1 >= after, "Each addBack() should create at most one new array");
      }
      assertTrue(NewObjectArray.NUM_CALLS < 2 * ((int) (Math.log(100) / Math.log(2))), "addBack() should not use an excessive number of new arrays");
      for (int i = 0; i < 100; i++) {
        int before = NewObjectArray.NUM_CALLS;
        impl.removeBack();
        int after = NewObjectArray.NUM_CALLS;
        assertTrue(before == after, "removeBack() should not allocate any new arrays");
      }
    }

    @Order(implSpecificTestLevel)
    @DisplayName("push should always succeed")
    @TestDescription("This test is checking every push in your ArrayDeque returns true.\nThe only time a push might return false is if it doesn't resize (like in the B Tests).")
    @DependsOn({"fields", "constructors", "push"})
    @Test
    public void testThatArrayDequePushAlwaysSucceeds() {
      ArrayDeque<Integer> impl = new ArrayDeque<>();
      for (int i = 0; i < 100; i++) {
        assertTrue(impl.push(i), "push() should always succeed for ArrayDeque");
      }
    }
  }
}