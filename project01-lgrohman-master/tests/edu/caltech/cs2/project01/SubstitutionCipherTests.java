package edu.caltech.cs2.project01;

import edu.caltech.cs2.helpers.Reflection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubstitutionCipherTests {
    private static String PANGRAM = "SPHINXOFBLACKQUARTZJUDGEMYVOW";
    public static Map<Character, Character> genIdentityMap() {
        return Map.ofEntries(Map.entry('A', 'A'),
                Map.entry('B', 'B'),
                Map.entry('C', 'C'),
                Map.entry('D', 'D'),
                Map.entry('E', 'E'),
                Map.entry('F', 'F'),
                Map.entry('G', 'G'),
                Map.entry('H', 'H'),
                Map.entry('I', 'I'),
                Map.entry('J', 'J'),
                Map.entry('K', 'K'),
                Map.entry('L', 'L'),
                Map.entry('M', 'M'),
                Map.entry('N', 'N'),
                Map.entry('O', 'O'),
                Map.entry('P', 'P'),
                Map.entry('Q', 'Q'),
                Map.entry('R', 'R'),
                Map.entry('S', 'S'),
                Map.entry('T', 'T'),
                Map.entry('U', 'U'),
                Map.entry('V', 'V'),
                Map.entry('W', 'W'),
                Map.entry('X', 'X'),
                Map.entry('Y', 'Y'),
                Map.entry('Z', 'Z'));
    }

    public static void swapSubstitutionCipherRandom(int seed) {
        // Replace the static random in the class with new seeded rand
        Field rand = Reflection.getFieldByType(SubstitutionCipher.class, Random.class);
        Reflection.<Random>getFieldValue(SubstitutionCipher.class, rand.getName(), null).setSeed(seed);
    }

    static Stream<Arguments> cipherTextKeyProvider() {
        return Stream.of(Arguments.of("", new HashMap<Character, Character>(), "", 0.0),
                Arguments.of("AAAAAAAAAAAAAAAAA", Map.ofEntries(Map.entry('A', 'A')), "AAAAAAAAAAAAAAAAA", -81.19073589942083),
                Arguments.of("AAAAA", Map.ofEntries(Map.entry('A', 'B')), "BBBBB", -14.11038824167156),
                Arguments.of("ABABAB", Map.ofEntries(Map.entry('A', 'B'),
                        Map.entry('B', 'A')), "BABABA", -16.570212114989623),
                Arguments.of("HELP", Map.ofEntries(Map.entry('H', 'S'),
                        Map.entry('E', 'A'), Map.entry('L', 'F'), Map.entry('P', 'E')), "SAFE", -4.218018222026446));
    }

    @Order(0)
    @Tag("B")
    @DisplayName("Test first constructor and getters")
    @ParameterizedTest(name = "ciphertext = {0}, key = {1}")
    @MethodSource("cipherTextKeyProvider")
    public void testConstructorAndGetters(String ciphertext, Map<Character, Character> key, String plaintext, double score) {
        SubstitutionCipher sc = new SubstitutionCipher(ciphertext, key);
        Assertions.assertEquals(ciphertext, sc.getCipherText(), "Returned ciphertext incorrect");
        Assertions.assertEquals(plaintext, sc.getPlainText(), "Returned plaintext incorrect");
    }

    @Order(1)
    @Tag("B")
    @DisplayName("Test getScore")
    @ParameterizedTest(name = "ciphertext = {0}, key = {1}, score = {3}")
    @MethodSource("cipherTextKeyProvider")
    public void testGetScore(String ciphertext, Map<Character, Character> key, String plaintext, double score) throws FileNotFoundException {
        SubstitutionCipher sc = new SubstitutionCipher(ciphertext, key);
        QuadGramLikelihoods qgl = new QuadGramLikelihoods();
        Assertions.assertEquals(score, sc.getScore(qgl), 1e-3);
    }

    @Order(2)
    @Tag("B")
    @DisplayName("Random is a static field")
    @Test
    public void testRandomStatic() {
        Field rand = Reflection.getFieldByType(SubstitutionCipher.class, Random.class);
        Reflection.checkFieldModifiers(rand, List.of("private", "static"));
    }

    @Order(3)
    @Tag("B")
    @DisplayName("Test randomSwap")
    @ParameterizedTest(name = "seed = {0}")
    @ValueSource(ints = {148, 327, 608, 610, 911})
    public void testRandomSwap(int seed) throws InvocationTargetException, IllegalAccessException, IOException {
        String filename = "tests/data/trace_seed_" + seed + ".txt";
        List<String> expectedOutput = Files.readAllLines(Paths.get(filename));
        int numSwaps = 5000;
        String ciphertext = "SPHINXOFBLACKQUARTZJUDGEMYVOW";

        swapSubstitutionCipherRandom(seed);
        Map<Character, Character> identityMap = genIdentityMap();

        SubstitutionCipher sc = new SubstitutionCipher(ciphertext, identityMap);
        for (int i = 0; i < numSwaps; i ++) {
            String expected = expectedOutput.get(i);
            sc = sc.randomSwap();
            Assertions.assertEquals(expected, sc.getPlainText(), "Incorrect plaintext after random swap");
        }
    }

    @Order(5)
    @Tag("B")
    @DisplayName("Test randomSwap with shorter cipher text")
    @ParameterizedTest(name = "seed = {0}")
    @ValueSource(ints = {148, 327, 608, 610, 911})
    public void testRandomSwapShort(int seed) throws InvocationTargetException, IllegalAccessException, IOException {
        String filename = "tests/data/trace2_seed_" + seed + ".txt";
        List<String> expectedOutput = Files.readAllLines(Paths.get(filename));
        int numSwaps = 10000;
        String ciphertext = "BLANK";

        swapSubstitutionCipherRandom(seed);

        SubstitutionCipher sc = new SubstitutionCipher(ciphertext);
        for (int i = 0; i < numSwaps; i ++) {
            String expected = expectedOutput.get(i);
            sc = sc.randomSwap();
            Assertions.assertEquals(expected, sc.getPlainText(), "Incorrect plaintext after random swap");
        }
    }

    @Order(4)
    @Tag("B")
    @DisplayName("Test constructor for random cipher")
    @ParameterizedTest(name = "seed = {0}")
    @CsvSource({"520, GILOXPFCUBSDAHJSZWRKJYEQNTVFM",
            "203, OZLDCIJMEHKARUTKGVFBTYWSNQXJP",
            "342, SZVHPAMLINRECFGRYWOUGKTDXQJMB",
            "426, NVOKFHEATPBWJZSBRIXCSQYLDUGEM",
            "99, CSREMNBVJZTHKOPTGIQLPDAFWXYBU"
    })
    public void testRandomSubstitutionCipherConstructor(int seed, String expectedPlainText) {
        swapSubstitutionCipherRandom(seed);
        SubstitutionCipher sc = new SubstitutionCipher(PANGRAM);
        Assertions.assertEquals(expectedPlainText, sc.getPlainText(), "Incorrect plaintext after random initialization");
    }

}
