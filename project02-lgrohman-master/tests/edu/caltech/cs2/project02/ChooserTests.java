package edu.caltech.cs2.project02;

import edu.caltech.cs2.helpers.CaptureSystemOutput;
import edu.caltech.cs2.helpers.FileSource;
import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.project02.choosers.EvilHangmanChooser;
import edu.caltech.cs2.project02.choosers.RandomHangmanChooser;
import edu.caltech.cs2.project02.guessers.ConsoleHangmanGuesser;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@CaptureSystemOutput
public class ChooserTests {
    private static String RANDOM_SOURCE = "src/edu/caltech/cs2/project02/choosers/RandomHangmanChooser.java";
    private static String EVIL_SOURCE = "src/edu/caltech/cs2/project02/choosers/EvilHangmanChooser.java";

    @Order(3)
    @Tag("C")
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java\\.lang\\.reflect", "Arrays", "java\\.util\\.ArrayList");
        Inspection.assertNoImportsOf(RANDOM_SOURCE, regexps);
        Inspection.assertNoUsageOf(RANDOM_SOURCE, regexps);
        Inspection.assertNoImportsOf(EVIL_SOURCE, regexps);
        Inspection.assertNoUsageOf(EVIL_SOURCE, regexps);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("Does not use Map as a field")
    @Test
    public void testNoMapFieldRHC() {
        Reflection.assertFieldsEqualTo(RandomHangmanChooser.class, Map.class, 0);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("The overall number of fields in RandomHangmanChooser is small")
    @Test
    public void testSmallNumberOfFieldsRHC() {
        Reflection.assertFieldsLessThan(RandomHangmanChooser.class, "private", 4);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("There are no public fields in RandomHangmanChooser")
    @Test
    public void testNoPublicFieldsRHC() {
        Reflection.assertNoPublicFields(RandomHangmanChooser.class);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("There are no protected fields in RandomHangmanChooser")
    @Test
    public void testNoProtectedFieldsRHC() {
        Reflection.assertNoProtectedFields(RandomHangmanChooser.class);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("All fields in RandomHangmanChooser have modifiers")
    @Test
    public void testFieldModifiersRHC() {
        Reflection.assertFieldModifiers(RandomHangmanChooser.class);
    }

    @Order(3)
    @Tag("C")
    @DisplayName("Chosen word and random are the only final fields")
    @Test
    public void testChosenWordFinal() {
        Reflection.assertFieldsEqualTo(RandomHangmanChooser.class, "final", 2);
        Reflection.assertFieldsEqualTo(RandomHangmanChooser.class, "final", Random.class, 1);
        Reflection.assertFieldsEqualTo(RandomHangmanChooser.class, "final", String.class, 1);

    }

    @Order(3)
    @Tag("C")
    @DisplayName("Random is a static field")
    @Test
    public void testRandomStatic() {
        Field rand = Reflection.getFieldByType(RandomHangmanChooser.class, Random.class);
        Reflection.checkFieldModifiers(rand, List.of("private", "static"));
    }

    @Order(3)
    @Tag("C")
    @DisplayName("Expected constructor exceptions for RandomHangmanChooser")
    @Test
    public void testExceptionsViolatedInRandomConstructor() {
        Constructor c = Reflection.getConstructor(RandomHangmanChooser.class, int.class, int.class);
        assertThrows(IllegalArgumentException.class, () -> Reflection.newInstance(c, -1, 3));
        assertThrows(IllegalArgumentException.class, () -> Reflection.newInstance(c, 3, -1));
        assertThrows(IllegalStateException.class, () -> Reflection.newInstance(c, Integer.MAX_VALUE, 3));
    }

    @Order(3)
    @Tag("C")
    @DisplayName("Expected makeGuess() exceptions for characters that aren't lower case for RandomHangmanChooser")
    @Test
    public void testMakeGuessExceptionsInRandom() {
        Constructor c = Reflection.getConstructor(RandomHangmanChooser.class, int.class, int.class);
        RandomHangmanChooser chooser = Reflection.newInstance(c, 3, 1);
        Method m = Reflection.getMethod(RandomHangmanChooser.class, "makeGuess", char.class);
        IntStream.range(0, 20).forEach(i -> assertThrows(IllegalArgumentException.class, () -> Reflection.invoke(m, chooser, (char) ('a' - (i + 1)))));
        IntStream.range(0, 20).forEach(i -> assertThrows(IllegalArgumentException.class, () -> Reflection.invoke(m, chooser, (char) ('z' + (i + 1)))));
    }

    public void runTestGame(Class<? extends IHangmanChooser> clazz, int wordLength, int wrongAnswersAllowed, String guesses) throws FileNotFoundException {
        Constructor<? extends IHangmanChooser> constructor = Reflection.getConstructor(clazz, int.class, int.class);
        HangmanGame.playGame(
                Reflection.newInstance(constructor, wordLength, wrongAnswersAllowed),
                new ConsoleHangmanGuesser(new Scanner(String.join("\n", guesses.split(""))))
        );
    }

    @Order(3)
    @Tag("C")
    @DisplayName("Expected game end after revealing word for RandomHangmanChooser")
    @Test
    public void testGameEndOnRevealWordRHC() throws FileNotFoundException {
        RandomHangmanChooser chooser = new RandomHangmanChooser(8, 1);
        chooser.getWord();
        assertThrows(IllegalStateException.class, () -> chooser.makeGuess('a'));
    }


    @Order(3)
    @Tag("C")
    @DisplayName("Test RandomHangmanChooser Full Game")
    @ParameterizedTest(name = "{0}")
    @FileSource(
            inputs = {
                    "{seed = 1337, word length = 3, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{seed = 1337, word length = 3, max wrong guesses = 10, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{seed = 2, word length = 3, max wrong guesses = 10, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{seed = 44, word length = 7, max wrong guesses = 6, guesses = debats}",
                    "{seed = 6, word length = 20, max wrong guesses = 16, guesses = aeioubcdfghjklmnpqrstvwxyz}",
                    "{seed = 19, word length = 14, max wrong guesses = 1, guesses = aeiou}",
                    "{seed = 19, word length = 14, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{seed = 239, word length = 9, max wrong guesses = 7, guesses = gsnreou}",
                    "{seed = 77, word length = 21, max wrong guesses = 4, guesses = iqzyx}",
                    "{seed = 1288, word length = 7, max wrong guesses = 1, guesses = negator}",
                    "{seed = 1972, word length = 5, max wrong guesses = 8, guesses = computer}",
                    "{seed = 1972, word length = 19, max wrong guesses = 3, guesses = xvcounterz}",
                    "{seed = 2019, word length = 8, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{seed = 2019, word length = 8, max wrong guesses = 7, guesses = redfqin}",
                    "{seed = 2019, word length = 8, max wrong guesses = 2, guesses = redfqin}",
            },
            outputFiles = {
                    "trace0.txt",
                    "trace1.txt",
                    "trace2.txt",
                    "trace3.txt",
                    "trace4.txt",
                    "trace5.txt",
                    "trace6.txt",
                    "trace7.txt",
                    "trace8.txt",
                    "trace9.txt",
                    "trace10.txt",
                    "trace11.txt",
                    "trace12.txt",
                    "trace13.txt",
                    "trace14.txt",
            }
    )
    public void testPlayGameWithRandomChooser(Map<String, String> arguments, String expectedOutput, CaptureSystemOutput.OutputCapture capture) throws FileNotFoundException {
        // Parse arguments
        int seed = Integer.parseInt(arguments.get("seed"));
        int length = Integer.parseInt(arguments.get("word length"));
        int wrongAllowed = Integer.parseInt(arguments.get("max wrong guesses"));
        String guesses = arguments.get("guesses");

        // Set Random field to correct seed
        Field rand = Reflection.getFieldByType(RandomHangmanChooser.class, Random.class);
        Reflection.<Random>getFieldValue(RandomHangmanChooser.class, rand.getName(), null).setSeed(seed);

        // Run the actual game
        runTestGame(RandomHangmanChooser.class, length, wrongAllowed, guesses);

        assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
    }


    @Order(2)
    @Tag("B")
    @DisplayName("Does not use Map as a field")
    @Test
    public void testNoMapFieldEHC() {
        Reflection.assertFieldsEqualTo(EvilHangmanChooser.class, Map.class, 0);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("The overall number of fields in EvilHangmanChooser is small")
    @Test
    public void testSmallNumberOfFieldsEHC() {
        Reflection.assertFieldsLessThan(EvilHangmanChooser.class, "private", 5);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("There are no public fields in EvilHangmanChooser")
    @Test
    public void testNoPublicFieldsEHC() {
        Reflection.assertNoPublicFields(EvilHangmanChooser.class);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("There are no protected fields in EvilHangmanChooser")
    @Test
    public void testNoProtectedFieldsEHC() {
        Reflection.assertNoProtectedFields(EvilHangmanChooser.class);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("All fields in EvilHangmanChooser have modifiers")
    @Test
    public void testFieldModifiersEHC() {
        Reflection.assertFieldModifiers(EvilHangmanChooser.class);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("There is no map field in EvilHangmanChooser")
    @Test
    public void testNoMapField() {
        if (Reflection.getFields(EvilHangmanChooser.class).filter(Reflection.hasType(Map.class)).findAny().isPresent()) {
            fail("You should not be storing the map as a field! Think about if the map is used in more than one method.");
        }
    }

    @Order(2)
    @Tag("B")
    @DisplayName("Expected constructor exceptions for EvilHangmanChooser")
    @Test
    public void testExceptionsViolatedInEvilConstructor() {
        Constructor c = Reflection.getConstructor(EvilHangmanChooser.class, int.class, int.class);
        assertThrows(IllegalArgumentException.class, () -> Reflection.newInstance(c, -1, 3));
        assertThrows(IllegalArgumentException.class, () -> Reflection.newInstance(c, 3, -1));
        assertThrows(IllegalStateException.class, () -> Reflection.newInstance(c, Integer.MAX_VALUE, 3));
    }

    @Order(2)
    @Tag("B")
    @DisplayName("Expected makeGuess() exceptions for characters that aren't lower case for EvilHangmanChooser")
    @Test
    public void testMakeGuessExceptionsInEvil() {
        Constructor c = Reflection.getConstructor(EvilHangmanChooser.class, int.class, int.class);
        EvilHangmanChooser chooser = Reflection.newInstance(c, 3, 1);
        Method m = Reflection.getMethod(EvilHangmanChooser.class, "makeGuess", char.class);
        IntStream.range(0, 20).forEach(i -> assertThrows(IllegalArgumentException.class, () -> Reflection.invoke(m, chooser, (char) ('a' - (i + 1)))));
        IntStream.range(0, 20).forEach(i -> assertThrows(IllegalArgumentException.class, () -> Reflection.invoke(m, chooser, (char) ('z' + (i + 1)))));
    }

    @Order(3)
    @Tag("B")
    @DisplayName("Expected game end after revealing word for EvilHangmanChooser")
    @Test
    public void testGameEndOnRevealWordEHC() throws FileNotFoundException {
        EvilHangmanChooser chooser = new EvilHangmanChooser(8, 1);
        chooser.getWord();
        assertThrows(IllegalStateException.class, () -> chooser.makeGuess('a'));
    }

    @Order(2)
    @Tag("B")
    @DisplayName("Test EvilHangmanChooser Full Game")
    @ParameterizedTest(name = "{0}")
    @FileSource(
            inputs = {
                    "{word length = 3, max wrong guesses = 10, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{word length = 3, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{word length = 5, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{word length = 5, max wrong guesses = 1, guesses = a}",
                    "{word length = 5, max wrong guesses = 10, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{word length = 15, max wrong guesses = 15, guesses = aeioubcdfghjklmnpqrstvwxyz}",
                    "{word length = 20, max wrong guesses = 15, guesses = aeioubcdfghjklmnpqrstvwxyz}",
                    "{word length = 8, max wrong guesses = 14, guesses = aeioubcdfghjklmnpqrstvwxyz}",
                    "{word length = 20, max wrong guesses = 1, guesses = u}",
                    "{word length = 7, max wrong guesses = 26, guesses = abcdefghijklmnopqrstuvwxyz}",
                    "{word length = 7, max wrong guesses = 5, guesses = tusor}",
                    "{word length = 7, max wrong guesses = 8, guesses = tusoraeiz}",
                    "{word length = 7, max wrong guesses = 7, guesses = ziearosut}",
                    "{word length = 4, max wrong guesses = 11, guesses = etaoinshrlud}",
                    "{word length = 12, max wrong guesses = 7, guesses = etaoinshrlud}",
            },
            outputFiles = {
                    "trace0-evil.txt",
                    "trace1-evil.txt",
                    "trace2-evil.txt",
                    "trace3-evil.txt",
                    "trace4-evil.txt",
                    "trace5-evil.txt",
                    "trace6-evil.txt",
                    "trace7-evil.txt",
                    "trace8-evil.txt",
                    "trace9-evil.txt",
                    "trace10-evil.txt",
                    "trace11-evil.txt",
                    "trace12-evil.txt",
                    "trace13-evil.txt",
                    "trace14-evil.txt",
            }
    )
    public void testPlayGameWithEvilChooser(Map<String, String> arguments, String expectedOutput, CaptureSystemOutput.OutputCapture capture) throws FileNotFoundException {
        // Parse arguments
        int length = Integer.parseInt(arguments.get("word length"));
        int wrongAllowed = Integer.parseInt(arguments.get("max wrong guesses"));
        String guesses = arguments.get("guesses");

        // Run the actual game
        runTestGame(EvilHangmanChooser.class, length, wrongAllowed, guesses);

        assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
    }
}
