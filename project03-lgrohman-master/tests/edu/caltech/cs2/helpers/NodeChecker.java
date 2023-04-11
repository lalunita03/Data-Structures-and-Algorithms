package edu.caltech.cs2.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.hamcrest.collection.IsIterableContainingInOrder;

import edu.caltech.cs2.interfaces.IDeque;

/**
 * @author Archie Shahidullah <archie@caltech.edu>
 */
public class NodeChecker {

    /**
     * This method checks whether a given class is a linked node or not.
     *
     * @param clazz        the class you want to check
     * @param doublyLinked whether or not the list <em>can</em> be doubly linked
     */
    public static void isNode(Class clazz, boolean doublyLinked) {
        // Check if class is private
        if (!Modifier.isPrivate(clazz.getModifiers())) {
            fail("Class " + clazz.getTypeName() + " is not private");
        }

        // Check if class is static
        if (!Modifier.isStatic(clazz.getModifiers())) {
            fail("Class " + clazz.getTypeName() + " is not static");
        }

        // Get fields
        SortedSet<String> fields = new TreeSet<>(
                Stream.of(clazz.getDeclaredFields()).map(x -> x.getName()).collect(Collectors.toList()));

        boolean hasData = false;
        boolean hasNode = false;

        // Check fields
        for (String field : fields) {
            Field f = null;
            try {
                f = clazz.getDeclaredField(field);
                f.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                fail();
            }

            if (f.getType().toString().equals("class " + clazz.getTypeName())) {
                if (hasNode && !doublyLinked) {
                    // Returns false is the list is doubly linked
                    fail("Class " + clazz.getName() + " is a doubly linked node");
                    return;
                }
                // Linked to another node
                hasNode = true;
            } else if (f.getType().toString().equals("class java.lang.Object")) {
                if (!Modifier.isFinal(f.getModifiers())) {
                    fail("Field \"" + field + "\" in class " + clazz.getName() + " is not final");
                }
                // Has a generic type to store data
                if (hasData) {
                    // Checks for multiple data fields
                    fail("Class " + clazz.getName() + " has multiple generic fields: \"" + field + "\"");
                    return;
                }
                hasData = true;
            } else {
                fail("Field \"" + field + "\" is not a generic type in " + clazz.getTypeName());
            }
        }

        // Get constructors
        Constructor[] constructors = clazz.getConstructors();

        // Checks arguments to the constructors
        for (Constructor c : constructors) {
            boolean hasConstructor = false;
            for (Class type : c.getParameterTypes()) {
                if (type.toString().equals("class java.lang.Object")) {
                    if (hasConstructor) {
                        // Checks for multiple arguments
                        fail("Constructor \"" + c.getName() + "\" has multiple generic arguments");
                        return;
                    }
                    hasConstructor = true;
                } else if (!type.toString().equals("class " + clazz.getTypeName())) {
                    // Check for invalid argument types
                    fail("Constructor \"" + c.getName() + "\" has an argument that is not a " + "generic type in class "
                            + clazz.getTypeName());
                }
            }
        }
    }

    /**
     * This method performs a node check on every internal class.
     *
     * @param clazz        the class you want to check
     * @param doublyLinked whether or not the list <em>can</em> be doubly linked
     */
    public static void checkInternalClasses(Class clazz, boolean doublyLinked) {
        Class[] classes = clazz.getDeclaredClasses();
        boolean node = false;
        for (Class c : classes) {
            String className = c.toString().replaceFirst("class ", "");
            try {
                isNode(Class.forName(className), doublyLinked);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                fail();
            } catch (AssertionError e) {
                continue;
            }
            node = true;
        }
        if (!node) {
            fail("There are no valid node classes in " + clazz.getName());
        }
    }

    /**
     * This method gets a valid, internal node class from a given class.
     *
     * @param clazz        the class you want to check
     * @param doublyLinked whether or not the list <em>can</em> be doubly linked
     * @return the node class
     */
    public static Class getNodeClass(Class clazz, boolean doublyLinked) {
        Class[] classes = clazz.getDeclaredClasses();
        for (Class c : classes) {
            try {
                isNode(c, doublyLinked);
            } catch (AssertionError e) {
                // Is not a node class so continue searching
                continue;
            }
            return c;
        }
        fail("There are no valid node classes in " + clazz.getName());
        // Should never reach here
        return null;
    }

    /**
     * This method gets fields of specified type from a given class.
     *
     * @param clazz the class you want to check
     * @param type  the type of field you want
     * @return a list of fields matching the given type
     */
    public static List<Field> getFields(Class clazz, Class type) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> namedFields = new ArrayList<>();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.getType().toString().equals("class " + type.getTypeName())) {
                namedFields.add(f);
            }
        }
        return namedFields;
    }

    /**
     * This method checks whether a given pointer permutation in a deque contains a
     * cycle.
     *
     * @param deque      the deque you want to check
     * @param nextField  the field corresponding to the next pointer in a linked
     *                   node
     * @param dequeField the field corresponding to the head pointer in a linked
     *                   deque
     * @param <E>        the generic type of the data field in a linked node
     * @return an array containing the indices of the cyclic nodes
     */
    public static <E> int[] checkCycle(IDeque<E> deque, Field nextField, Field dequeField) {
        // Grab head of list
        Object head = null;
        try {
            head = dequeField.get(deque);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            fail();
        }
        // Create array to store all nodes
        Object[] nodes = new Object[deque.size() + 1];
        // Iterate through list
        Object temp = head;
        int i = 0;
        while (temp != null) {
            nodes[i] = temp;
            for (int j = 0; j < i; j++) {
                // Check if memory locations are equal
                if (nodes[j] == nodes[i]) {
                    // Return indices of nodes that create a cycle
                    return new int[] { i, j };
                }
            }
            try {
                // Next node
                temp = nextField.get(temp);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                fail();
            }
            i++;
        }
        // No cycle
        return new int[] { -1, -1 };
    }

    /**
     * This method checks whether a given deque contains a cycle.
     *
     * @param deque        the deque you want to check
     * @param doublyLinked whether or not the list <em>can</em> be doubly linked
     * @param <E>          the generic type of the data field in a linked node
     */
    public static <E> void cycleDetection(IDeque<E> deque, boolean doublyLinked) {
        Class nodeClass = getNodeClass(deque.getClass(), doublyLinked);
        // Can be either next or previous pointer
        List<Field> nextFields = getFields(nodeClass, nodeClass);
        // Can be either head or tail pointer
        List<Field> dequeFields = getFields(deque.getClass(), nodeClass);
        // Check all permutations of pointers
        int[] nodes;
        for (Field nextField : nextFields) {
            for (Field dequeField : dequeFields) {
                // Check for a cycle
                nodes = checkCycle(deque, nextField, dequeField);
                if (nodes[0] == -1 && nodes[1] == -1) {
                    // No cycle
                    continue;
                }
                if (nodes[0] == deque.size() && nodes[1] == 0) {
                    fail("The last node is connected to the first node in " + nodeClass.getName() + " object");
                } else {
                    fail("Node " + nodes[0] + " is connected to Node " + nodes[1] + " in " + nodeClass.getName()
                            + " object");
                }
            }
        }
    }

    /**
     * This method checks whether iterating through a list forwards and backwards
     * returns the same values.
     *
     * @param deque the deque you want to check
     * @param <E>   the generic type of the data field in a linked node
     */
    public static <E> void checkReverse(IDeque<E> deque) {
        // Grab the linked node class and possible pointers to the head and tail
        Class nodeClass = getNodeClass(deque.getClass(), true);
        List<Field> dequePointers = getFields(deque.getClass(), nodeClass);
        assertEquals(2, dequePointers.size(), "List does not have one head and tail pointer");
        // Try all permutations of pointers
        try {
            for (int i = 0; i < 2; i++) {
                // Get trial head and tail pointers
                Field headField = dequePointers.get(i);
                Field tailField = dequePointers.get((i + 1) % 2);
                Object head = headField.get(deque);
                Object tail = tailField.get(deque);
                // If deque size is one, tests will fail so check alternative
                if (deque.size() == 1) {
                    assertEquals(head, tail, "Deque of size 1 does not have same head and tail");
                    return;
                }
                // Grab possible next and previous pointers
                List<Field> pointers = getFields(head.getClass(), nodeClass);
                assertEquals(2, pointers.size(), "List is not doubly linked");
                for (int j = 0; j < 2; j++) {
                    // Get trial next and previous pointers
                    Field next = pointers.get(j);
                    Field prev = pointers.get((j + 1) % 2);
                    // Get data field
                    List<Field> dataFields = getFields(nodeClass, Object.class);
                    assertEquals(1, dataFields.size(), "Incorrect number of generic types in node class");
                    Field data = dataFields.get(0);
                    // Iterate through linked list and construct value lists
                    List<E> forwardValues = new ArrayList<>();
                    List<E> backwardValues = new ArrayList<>();
                    Object temp = head;
                    while (temp != null) {
                        forwardValues.add((E) data.get(temp));
                        temp = next.get(temp);
                    }
                    temp = tail;
                    while (temp != null) {
                        backwardValues.add((E) data.get(temp));
                        temp = prev.get(temp);
                    }
                    Collections.reverse(backwardValues);
                    // Test the reverse of the backwards equals the forwards
                    if (IsIterableContainingInOrder.contains(forwardValues.toArray()).matches(backwardValues)) {
                        return;
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            fail();
        }
        // Exiting the loop indicates failure
        fail("Forwards and backwards lists of values do not agree");
    }

}
