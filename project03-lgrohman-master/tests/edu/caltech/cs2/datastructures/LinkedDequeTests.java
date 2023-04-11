package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("C")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
public class LinkedDequeTests {
  private static String LINKED_DEQUE_SOURCE = "src/edu/caltech/cs2/datastructures/LinkedDeque.java";

  private Constructor linkedDequeConstructor = Reflection.getConstructor(LinkedDeque.class);

  // LINKEDDEQUE-SPECIFIC TESTS ----------------------------------------
  @DisplayName("Style")
  @Nested
  class StyleTests implements IStyleTests {
    @Order(classSpecificTestLevel)
    @DisplayName("There are no static fields")
    @TestHint("Remember that static fields belong to the class instead of the instance.  For this class, you shouldn't need any.")
    @Test
    public void testConstantFields() {
      Reflection.assertFieldsEqualTo(LinkedDeque.class, "static", 0);
    }

    @Override
    public String getSource() {
      return LINKED_DEQUE_SOURCE;
    }

    @Override
    public Class<?> getClazz() {
      return LinkedDeque.class;
    }

    @Override
    public List<String> getPublicInterface() {
      return List.of("addFront", "addBack", "removeFront", "removeBack",
              "enqueue", "dequeue", "push", "pop", "peek", "peekFront", "peekBack", "iterator", "size", "toString");
    }

    @Override
    public int getMaxFields() {
      return 4;
    }

    @Override
    public List<String> methodsToBanSelf() {
      return List.of("addFront", "addBack", "removeBack", "removeFront", "iterator");
    }

  }

  @Nested
  @DisplayName("Runtime Complexity")
  class RuntimeTests {
    @Order(complexityTestLevel)
    @DisplayName("addFront() and removeFront() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testFrontDequeOperationComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new LinkedDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addFront(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> addFront = (IDeque<Integer> q) -> q.addFront(0);
      Consumer<IDeque<Integer>> removeFront = (IDeque<Integer> q) -> q.removeFront();

      RuntimeInstrumentation.assertAtMost("addFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addFront,
              8);
      RuntimeInstrumentation.assertAtMost("removeFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide,
              removeFront, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("addBack() and removeBack() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testBackDequeOperationComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new LinkedDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addBack(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> addBack = (IDeque<Integer> q) -> q.addBack(0);
      Consumer<IDeque<Integer>> removeBack = (IDeque<Integer> q) -> q.removeBack();

      RuntimeInstrumentation.assertAtMost("addBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addBack, 8);
      RuntimeInstrumentation.assertAtMost("removeBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide,
              removeBack, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("enqueue() and dequeue() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testQueueOperationComplexity() {
      Function<Integer, IQueue<Integer>> provide = (Integer numElements) -> {
        IQueue<Integer> q = new LinkedDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.enqueue(i);
        }
        return q;
      };
      Consumer<IQueue<Integer>> enqueue = (IQueue<Integer> q) -> q.enqueue(0);
      Consumer<IQueue<Integer>> dequeue = (IQueue<Integer> q) -> q.dequeue();

      RuntimeInstrumentation.assertAtMost("enqueue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, enqueue, 8);
      RuntimeInstrumentation.assertAtMost("dequeue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, dequeue, 8);
    }

    @Order(complexityTestLevel)
    @DisplayName("push() and pop() take constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testStackOperationComplexity() {
      Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
        IStack<Integer> q = new LinkedDeque<>();
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
        IStack<Integer> q = new LinkedDeque<>();
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
    @Test
    public void testPeekFrontComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new LinkedDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addFront(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> peekFront = (IDeque<Integer> q) -> q.peekFront();

      RuntimeInstrumentation.assertAtMost("peekFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekFront,
              8);
    }

    @Order(complexityTestLevel)
    @DisplayName("peekBack() takes constant time")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    public void testPeekBackComplexity() {
      Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
        IDeque<Integer> q = new LinkedDeque<>();
        for (int i = 0; i < numElements; i++) {
          q.addBack(i);
        }
        return q;
      };
      Consumer<IDeque<Integer>> peekBack = (IDeque<Integer> q) -> q.peekBack();

      RuntimeInstrumentation.assertAtMost("peekBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekBack,
              8);
    }
  }

  @Nested
  @DisplayName("IStack Functionality")
  class StackTests implements IStackTests {
    @Override
    public IStack<Object> newStack() {
      return Reflection.newInstance(linkedDequeConstructor);
    }
  }

  @Nested
  @DisplayName("IQueue Functionality")
  class QueueTests implements IQueueTests {
    public IQueue<Object> newQueue() {
      return Reflection.newInstance(linkedDequeConstructor);
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
      return Reflection.newInstance(linkedDequeConstructor);
    }
  }

  @Nested
  @DisplayName("ICollection Functionality")
  class CollectionTests implements ICollectionTests {
    @Override
    public ICollection<Object> newCollection() {
      return Reflection.newInstance(linkedDequeConstructor);
    }
  }

  @DisplayName("Implementation Tests")
  @Nested
  class ImplementationTests {
    @Order(implSpecificTestLevel)
    @DisplayName("Check for excessive node allocation in addFront and removeFront")
    @TestDescription("This test is checking that you are not allocating extra nodes in add/remove that are not necessary.")
    @DependsOn({"fields", "constructors", "addFront", "removeFront"})
    @Test
    public void testForExcessiveNodeAllocationAddFront() {
      NewNode.NUM_CALLS = 0;
      edu.caltech.cs2.datastructures.LinkedDeque<Integer> impl = new edu.caltech.cs2.datastructures.LinkedDeque<>();
      for (int i = 0; i < 100; i++) {
        int before = NewNode.NUM_CALLS;
        impl.addFront(i);
        int after = NewNode.NUM_CALLS;
        assertTrue(before + 1 >= after, "Each addFront() should create at most one new node");
      }
      for (int i = 0; i < 100; i++) {
        int before = NewNode.NUM_CALLS;
        impl.removeFront();
        int after = NewNode.NUM_CALLS;
        assertTrue(before == after, "removeFront() should not allocate any new nodes");
      }
    }

    @Order(implSpecificTestLevel)
    @DisplayName("Check for excessive node allocation in addBack and removeBack")
    @TestDescription("This test is checking that you are not allocating extra nodes in add/remove that are not necessary.")
    @DependsOn({"fields", "constructors", "addBack", "removeBack"})
    @Test
    public void testForExcessiveNodeAllocationAddBack() {
      NewNode.NUM_CALLS = 0;
      edu.caltech.cs2.datastructures.LinkedDeque<Integer> impl = new edu.caltech.cs2.datastructures.LinkedDeque<>();
      for (int i = 0; i < 100; i++) {
        int before = NewNode.NUM_CALLS;
        impl.addBack(i);
        int after = NewNode.NUM_CALLS;
        assertTrue(before + 1 >= after, "Each addBack() should create at most one new node");
      }
      for (int i = 0; i < 100; i++) {
        int before = NewNode.NUM_CALLS;
        impl.removeBack();
        int after = NewNode.NUM_CALLS;
        assertTrue(before == after, "removeBack() should not allocate any new nodes");
      }
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Check that LinkedDeque uses a node class")
    @TestDescription("This test is checking that you are are using a doubly-linked-list rather than some other implementation.")
    @Test
    public void testLinkedNode() {
      Class[] classes = LinkedDeque.class.getDeclaredClasses();
      boolean found = false;
      for (Class clazz : classes) {
        if (Iterator.class.isAssignableFrom(clazz)) {
          continue;
        }
        NodeChecker.isNode(clazz, true);
        found = true;
      }
      if (!found) {
        fail("There is no node class defined in LinkedDeque.");
      }
    }

    // "LINKED-NESS" TESTS ------------------------------------------------

    @Order(dequeTestLevel)
    @DisplayName("Cycle detection for addFront(...), addBack(...), removeFront(...), and removeBack(...)")
    @TestDescription("This test follows all the links in your linked list and checks if any of them result in a cycle.")
    @ParameterizedTest(name = "Test cycles - {1} random numbers with seed = {0}")
    @CsvSource({"69, 2000", "20, 3000"})
    public void checkForCycles(int seed, int size) {
      Random r = new Random(seed);
      Deque<Object> reference = new java.util.ArrayDeque<>();
      IDeque<Object> impl = new LinkedDeque<>();
      // Test that first peek is null
      assertNull(impl.peekFront(), "empty peek should return null");
      // Randomly add / remove elements to the front / back
      for (int i = 0; i < size; i++) {
        int num = r.nextInt();
        if (num % 2 == 0) {
          reference.addLast(num);
          impl.addBack(num);
        } else {
          reference.addFirst(num);
          impl.addFront(num);
        }
        if (reference.size() > 1 && impl.size() > 1) {
          if (num % 5 == 0) {
            reference.removeFirst();
            impl.removeFront();
          } else if (num % 7 == 0) {
            reference.removeLast();
            impl.removeBack();
          }
        }
        // After each operation, check whether cycles have formed
        NodeChecker.cycleDetection(impl, true);
        // Sanity checks, though these aren't super necessary
        assertEquals(reference.size(), impl.size(), "size()s are not equal");
        assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
      }
    }

    @Order(dequeTestLevel)
    @DisplayName("Check reverses for addFront(...), addBack(...), removeFront(...), and removeBack(...)")
    @TestDescription("This test follows all the links forwards and backwards to check that your links are consistent.")
    @ParameterizedTest(name = "Test reverse - {1} random numbers with seed = {0}")
    @CsvSource({"31, 2000", "64, 3000"})
    public void checkReverses(int seed, int size) {
      Random r = new Random(seed);
      Deque<Object> reference = new java.util.ArrayDeque<>();
      IDeque<Object> impl = new LinkedDeque<>();
      // Test that first peek is null
      assertNull(impl.peekFront(), "empty peek should return null");
      // Randomly add / remove elements to the front / back
      for (int i = 0; i < size; i++) {
        int num = r.nextInt();
        if (num % 2 == 0) {
          reference.addLast(num);
          impl.addBack(num);
        } else {
          reference.addFirst(num);
          impl.addFront(num);
        }
        if (reference.size() > 1 && impl.size() > 1) {
          if (num % 5 == 0) {
            reference.removeFirst();
            impl.removeFront();
          } else if (num % 7 == 0) {
            reference.removeLast();
            impl.removeBack();
          }
        }
        // Check that forwards and backwards iteration are sane
        NodeChecker.checkReverse(impl);
        assertEquals(reference.size(), impl.size(), "size()s are not equal");
        assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
      }
    }
  }
}