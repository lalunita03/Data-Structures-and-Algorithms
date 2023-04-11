package edu.caltech.cs2.project02;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.project02.guessers.AIHangmanGuesser;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

public class GuesserTests {
    private static String GUESSER_SOURCE = "src/edu/caltech/cs2/project02/guessers/AIHangmanGuesser.java";

    @Order(1)
    @Tag("A")
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.lang.reflect", "Arrays");
        Inspection.assertNoImportsOf(GUESSER_SOURCE, regexps);
        Inspection.assertNoUsageOf(GUESSER_SOURCE, regexps);
    }

    @Order(1)
    @Tag("A")
    @DisplayName("AIHangmanGuesser has no instance fields")
    @Test
    public void testNoFields() {
        Reflection.assertFieldsLessThan(AIHangmanGuesser.class, "private", 1);
    }

    @Order(1)
    @Tag("A")
    @DisplayName("There are no public fields in AIHangmanGuesser")
    @Test
    public void testNoPublicFieldsAIHG() {
        Reflection.assertNoPublicFields(AIHangmanGuesser.class);
    }

    @Order(1)
    @Tag("A")
    @DisplayName("There are no protected fields in AIHangmanGuesser")
    @Test
    public void testNoProtectedFieldsAIHG() {
        Reflection.assertNoProtectedFields(AIHangmanGuesser.class);
    }

    @Order(1)
    @Tag("A")
    @DisplayName("All fields in AIHangmanGuesser have modifiers")
    @Test
    public void testFieldModifiersAIHG() {
        Reflection.assertFieldModifiers(AIHangmanGuesser.class);
    }

    @Order(1)
    @Tag("A")
    @DisplayName("Test that the dictionary is static")
    @Test
    public void testDictionaryModifiers() {
        Field dictField = Reflection.getFieldByType(AIHangmanGuesser.class, String.class);
        Reflection.checkFieldModifiers(dictField, List.of("private", "static"));
    }

    @Order(1)
    @Tag("A")
    @DisplayName("Test getGuess Method in AIHangmanGuesser")
    @Test
    public void testGetGuess() throws FileNotFoundException {
        AIHangmanGuesser guesser = new AIHangmanGuesser();

        // test character with most occurrences is chosen
        assertEquals('e', guesser.getGuess("---", Set.of('a')));
        assertEquals('i', guesser.getGuess("---", Set.of('a', 'e', 'o')));
        assertEquals('e', guesser.getGuess("sc--nc-", Set.of('s', 'n', 'c')));
        // test first character in alphabetical order is chosen
        assertEquals('b', guesser.getGuess("-ee", Set.of('e')));
        assertEquals('a', guesser.getGuess("-ppl-", Set.of('p', 'l')));
        // test only correct letter is chosen
        assertEquals('g', guesser.getGuess("en-ineerin-", Set.of('e', 'n', 'i', 'r')));
        // test that only words matching the pattern are sampled (otherwise, returns 't')
        assertEquals('u', guesser.getGuess("---s", Set.of('a', 'e', 'i', 'o', 's')));
    }

}
