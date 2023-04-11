package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IStyleTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Method;
import java.util.List;

import static edu.caltech.cs2.project05.Project05TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
@Tag("A")
@ExtendWith(TestExtension.class)
public class BSTDictionaryTests extends IDictionaryNGramTests {
    @Override
    public int SINGLE_OP_TIMEOUT_MS() {
        return 50;
    }

    @Override
    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LOGARITHMIC;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityWorst() {
        return RuntimeInstrumentation.ComplexityType.LINEAR;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexityBest() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @Override
    public IDictionary<Object, Object> newIDictionary() {
        return (IDictionary<Object, Object>) (IDictionary<? extends Object, Object>) newBSTDictionary();
    }

    public BSTDictionary<Comparable<Object>, Object> newBSTDictionary() {
        return new BSTDictionary<>();
    }

    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/BSTDictionary.java";

    @DisplayName("Style")
    @Nested
    @Order(100)
    @Tag("A")
    class StyleTests implements IStyleTests {
        public void testNoProtectedFields() {
        }

        @Override
        public String getSource() {
            return DICTIONARY_SOURCE;
        }

        @Override
        public Class<?> getClazz() {
            return BSTDictionary.class;
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

    @DisplayName("Implementation")
    @Nested
    @Order(0)
    @Tag("A")
    class ImplementationTests {
        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive node allocation in put")
        @Test
        @TestDescription("This test is checking that you are not allocating extra nodes in put that are not necessary.")
        @DependsOn({"put"})
        public void testForExcessiveNodeAllocationPut() {
            NewNode.BSTDictionary_NUM_CALLS = 0;
            BSTDictionary<Integer, Integer> impl = new BSTDictionary<>();

            for (int j = 0; j < 100; j++) {
                int before = NewNode.BSTDictionary_NUM_CALLS;
                impl.put(j, j);
                int after = NewNode.BSTDictionary_NUM_CALLS;
                assertTrue(before + 1 >= after, "Each put() should create at most one new node");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive node allocation in get")
        @Test
        @TestDescription("This test is checking that you are not allocating extra nodes in get that are not necessary.")
        @DependsOn({"put", "get"})
        @TestHint("get should not be affecting the dictionary in any way, it should simply find and return a value.")
        public void testForExcessiveNodeAllocationGet() {
            NewNode.BSTDictionary_NUM_CALLS = 0;
            BSTDictionary<Integer, Integer> impl = new BSTDictionary<>();

            for (int j = 0; j < 100; j++) {
                impl.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewNode.BSTDictionary_NUM_CALLS;
                impl.get(j);
                int after = NewNode.BSTDictionary_NUM_CALLS;
                assertEquals(before, after, "get() should not allocate any new node");
            }
        }

        @Order(classSpecificTestLevel)
        @DisplayName("Check for excessive Node allocation in remove")
        @Test
        @TestDescription("This test is checking that you are not allocating extra nodes in remove that are not necessary.")
        @DependsOn({"put", "remove"})
        public void testForExcessiveNodeAllocationRemove() {
            NewNode.BSTDictionary_NUM_CALLS = 0;
            BSTDictionary<Integer, Integer> impl = new BSTDictionary<>();

            for (int j = 0; j < 100; j++) {
                impl.put(j, j);
            }

            for (int j = 0; j < 100; j++) {
                int before = NewNode.BSTDictionary_NUM_CALLS;
                impl.remove(j);
                int after = NewNode.BSTDictionary_NUM_CALLS;
                assertEquals(after, before, "remove() should not allocate any new node");
            }
        }

        /*
        TODO: this test does not work to the confidence level Adam is happy with.
              pls fix next year.
        @Order(classSpecificTestLevel)
        @DisplayName("Check that remove() is implemented recursively")
        @Test
        public void testForRecursiveRemove() {
            Method solveMethod = Reflection.getMethod(BSTDictionary.class, "remove", Object.class);
            Inspection.assertRecursive(DICTIONARY_SOURCE, solveMethod);
        }
        */

        @Test
        @Order(classSpecificTestLevel)
        @DisplayName("Test binary search tree is structured correctly")
        @TestDescription("This test is testing sample keys and values to make sure the BST stores the values correctly.")
        @DependsOn({"put"})
        public void testActualBST() {
            BSTDictionary<String, Integer> bst = new BSTDictionary<>();

            bst.put("m", 1);
            bst.put("s", 2);
            bst.put("x", 3);
            bst.put("i", 4);
            bst.put("a", 5);
            bst.put("p", 6);
            bst.put("u", 7);
            bst.put("y", 8);
            bst.put("t", 9);

            assertEquals("{m: 1, i: 4, s: 2, a: 5, p: 6, x: 3, u: 7, y: 8, t: 9}", bst.toString(), "Incorrect binary search tree implementation");

        }
    }
}
