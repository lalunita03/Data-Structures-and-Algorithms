package edu.caltech.cs2.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author Archie Shahidullah <archie@caltech.edu>
 */
public class MoveToFrontChecker {

    /**
     * This method checks whether a given class is a linked node or not.
     *
     * @param clazz the class you want to check
     */
    public static void isNode(Class clazz) {
        // Check if class is private
        if (!Modifier.isPrivate(clazz.getModifiers())) {
            fail("Class " + clazz.getTypeName() + " is not private");
        }

        // Check if class is static
        if (!Modifier.isStatic(clazz.getModifiers())) {
            fail("Class " + clazz.getTypeName() + " is not static");
        }

        // Get fields
        SortedSet<String> fields = new TreeSet<>(Stream.of(clazz.getDeclaredFields())
                .map(x -> x.getName())
                .collect(Collectors.toList()));

        int hasData = 0;
        boolean hasNode = false;

        // Check fields
        for (String field : fields) {
            Field f = null;
            try {
                f = clazz.getDeclaredField(field);
                f.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            }

            if (f.getType().toString().equals("class " + clazz.getTypeName())) {
                if (hasNode) {
                    // Returns false is the list is doubly linked
                    fail("Class \"" + clazz.getName() + "\" is a doubly linked node");
                    return;
                }
                // Linked to another node
                hasNode = true;
            } else if (f.getType().toString().equals("class java.lang.Object")) {
                // Value can't be final! It's a _dictionary_, so we need to be able to update the value at a given Node
//                if (!Modifier.isFinal(f.getModifiers())) {
//                    fail("Field \"" + field + "\" in class \"" + clazz.getName() + "\" is not final");
//                }
                // Has a generic type to store data
                if (hasData > 2) {
                    // Checks for multiple data fields
                    fail("Class \"" + clazz.getName() + "\" has too many generic fields: \"" + field + "\"");
                    return;
                }
                hasData++;
            } else {
                fail("Field \"" + field + "\" is not a generic type or " + clazz.getTypeName());
            }
        }

        if (hasData != 2) {
            fail(clazz.getName() + " does not have two generic types as key and value");
        }

        // Get constructors
        Constructor[] constructors = clazz.getConstructors();

        // Checks arguments to the constructors
        for (Constructor c : constructors) {
            int hasConstructor = 0;
            for (Class type : c.getParameterTypes()) {
                if (type.toString().equals("class java.lang.Object")) {
                    if (hasConstructor > 2) {
                        // Checks for multiple arguments
                        fail("Constructor \"" + c.getName() + "\" has too many generic arguments");
                        return;
                    }
                    hasConstructor++;
                } else if (!type.toString().equals("class " + clazz.getTypeName())) {
                    // Check for invalid argument types
                    fail("Constructor \"" + c.getName() + "\" has an argument that is not a " + "generic type or "
                            + clazz.getTypeName());
                }
            }
            if (hasConstructor != 2) {
                fail("Constructor \"" + c.getName() + "\" does not have two generic type arguments");
            }
        }

    }

    public static Class getNodeClass(Class clazz) {
        Class[] classes = clazz.getDeclaredClasses();
        for (Class c : classes) {
            try {
                isNode(c);
            } catch (AssertionError e) {
                // Is not a node class so continue searching
                continue;
            }
            return c;
        }
        fail("There are no valid node classes in " + clazz.getName() +
                ". Make sure the test \"Check for linked node class\" passes.");
        // Should never reach here
        return null;
    }

    public static void checkClass(Class clazz) {
        // Get fields
        SortedSet<String> fields = new TreeSet<>(Stream.of(clazz.getDeclaredFields())
                .map(x -> x.getName())
                .collect(Collectors.toList()));

        // Get node class
        Class nodeClass = getNodeClass(clazz);

        boolean hasHead = false;
        boolean hasSize = false;

        /*
         * Ensure the implementation only has a head and int field
         * Primarily to deter keeping separate key and value lists
         */
        for (String field : fields) {
            Field f = null;
            try {
                f = clazz.getDeclaredField(field);
                f.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            }
            if (f.getType().toString().equals(int.class.getName())) {
                if (hasSize) {
                    fail("Class \"" + clazz.getName() + "\" has multiple int fields");
                }
                hasSize = true;
            }
            else if (f.getType().toString().equals("class " + nodeClass.getName())) {
                if (hasHead) {
                    fail("Class \"" + clazz.getName() + "\" has multiple \"" +
                            nodeClass.getName() + "\" fields");
                }
                hasHead = true;
            }
            else {
                fail("\"" + field + "\" is not a node object or int");
            }
        }
    }

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

}
