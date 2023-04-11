package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.datastructures.BSTDictionary;
import edu.caltech.cs2.datastructures.BSTDictionaryTests;
import edu.caltech.cs2.datastructures.MoveToFrontDictionary;
import edu.caltech.cs2.helpers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static edu.caltech.cs2.project05.Project05TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface IStyleTests {
    String getSource();
    Class<?> getClazz();
    List<String> getPublicInterface();
    int getMaxFields();
    List<String> methodsToBanSelf();

    @Order(classSpecificTestLevel)
    @DisplayName("The overall number of fields is small")
    @TestHint("It's good style to use the smallest number of fields that you can in each class.")
    @Test
    default void testSmallNumberOfFields() {
        Reflection.assertFieldsLessThan(getClazz(), "private", getMaxFields());
    }


    @Order(classSpecificTestLevel)
    @DisplayName("The public interface is correct")
    @TestDescription("This test checks that you haven't declared any extra public methods.")
    @TestHint("You are welcome to make any extra methods you want, but they have to be private!")
    @Test
    default void testPublicInterface() {
        Reflection.assertPublicInterface(getClazz(), getPublicInterface());
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Does not use or import disallowed classes")
    @TestHint("Remember that you're not allowed to use anything in java.util except Iterator and Random!")
    @Test
    default void testForInvalidClasses() {
        List<String> allowed = List.of("Iterator", "function\\.Supplier", "stream\\.Stream", "Random");
        Inspection.assertNoImportsOfExcept(getSource(), "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(getSource(), bannedUsages);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Does not attempt to get around constructor counts")
    @Test
    default void estForAvoidCounters() {
        List<String> regexps = List.of("NUM_CALLS");
        Inspection.assertNoUsageOf(getSource(), regexps);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Uses this(...) notation in all but one constructor")
    @TestDescription("This test is checking that all of your constructors except one call the other constructors rather than duplicating code.")
    @Test
    default void testForThisConstructors() {
        Inspection.assertConstructorHygiene(getSource());
    }

    @Order(classSpecificTestLevel)
    @DisplayName("All fields have modifiers")
    @TestDescription("This test checks that every field is private or public.")
    @Test
    default void testFieldModifiers() {
        Reflection.assertFieldModifiers(getClazz());
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no public fields")
    @TestHint("It's good style to never use public fields whenever avoidable.")
    @Test
    default void testNoPublicFields() {
        Reflection.assertNoPublicFields(getClazz());
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no protected fields")
    @TestHint("It's good style to never use protected fields whenever avoidable.")
    @Test
    default void testNoProtectedFields() {
        Reflection.assertNoProtectedFields(getClazz());
    }

    default Object getArg(Class<?> cl, Class<?> inspected) {
        if (cl == Function.class) {
            Function<IDeque, Object> collector = (IDeque d) -> List.of();
            return collector;
        } else if (cl == Comparable.class || (inspected == BSTDictionary.class && cl == Object.class)) {
            return "hello";
        }
        else if (cl == Iterable.class) {
            return List.of();
        }
        else if (cl == Object.class) {
            return List.of();
        }
        else if (cl == Supplier.class) {
            Supplier<IDictionary<Object, Object>> sup = () -> {
                return new MoveToFrontDictionary<>();
            };
            return sup;
        }
        else {
            return 0;
        }
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no attempts to create a new version of the data structure to avoid actually implementing a method")
    @Test
    default void testNoInstantiationOfSelf() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Method[] methods = getClazz().getDeclaredMethods();
        for (Method m : methods) {
            if (methodsToBanSelf().contains(m.getName())) {
                NewMe.NUM_CALLS = 0;
                Class<?> clazz = getClazz();
                Constructor<?>[] constructors = clazz.getConstructors();
                Object o = null;
                for (Constructor constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        o = constructor.newInstance();
                        break;
                    } else if (constructor.getParameterCount() == 1) {
                        Class<?>[] clazes = constructor.getParameterTypes();
                        Object arg = getArg(clazes[0], clazz);
                        o = constructor.newInstance(arg);
                        break;
                    }
                }

                if (Modifier.isPublic(m.getModifiers())) {
                    try {
                        for (int i = 0; i < 100; i++) {
                            if (m.getParameterCount() == 0) {
                                m.invoke(o);
                            } else if (m.getParameterCount() == 1) {
                                Class<?>[] clazes = m.getParameterTypes();
                                m.invoke(o, getArg(clazes[0], clazz));
                            } else if (m.getParameterCount() == 2) {
                                Class<?>[] clazes = m.getParameterTypes();
                                m.invoke(o, getArg(clazes[0], clazz), getArg(clazes[1], clazz));
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("You have a method that should be private that is public.");
                        System.out.println("Take a look at your method: " + m);
                        throw e;
                    }
                }
                assertTrue(NewMe.NUM_CALLS == 0, "Calls to " + m.getName() + " should not create a new instance of " + getClazz().getName());
            }
        }
    }



}
