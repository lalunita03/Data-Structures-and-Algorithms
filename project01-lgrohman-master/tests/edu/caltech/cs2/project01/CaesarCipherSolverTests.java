package edu.caltech.cs2.project01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import edu.caltech.cs2.helpers.Inspection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CaesarCipherSolverTests {
    String STRING_SOURCE = "src/edu/caltech/cs2/project01/CaesarCipherSolver.java";

    static final String DECLARATION = "WHEN IN THE COURSE OF HUMAN EVENTS IT BECOMES NECESSARY FOR ONE PEOPLE TO DISSOLVE THE " +
            "POLITICAL BANDS WHICH HAVE CONNECTED THEM WITH ANOTHER AND TO ASSUME AMONG THE POWERS OF THE EARTH THE " +
            "SEPARATE AND EQUAL STATION TO WHICH THE LAWS OF NATURE AND OF NATURES GOD ENTITLE THEM A DECENT RESPECT TO " +
            "THE OPINIONS OF MANKIND REQUIRES THAT THEY SHOULD DECLARE THE CAUSES WHICH IMPEL THEM TO THE SEPARATION";

    static final String CONSTITUTION = "WE THE PEOPLE OF THE UNITED STATES IN ORDER TO FORM A MORE PERFECT UNION ESTABLISH " +
            "JUSTICE INSURE DOMESTIC TRANQUILITY PROVIDE FOR THE COMMON DEFENCE PROMOTE THE GENERAL WELFARE AND SECURE " +
            "THE BLESSINGS OF LIBERTY TO OURSELVES AND OUR POSTERITY DO ORDAIN AND ESTABLISH THIS CONSTITUTION FOR THE " +
            "UNITED STATES OF AMERICA";

    @Order(0)
    @Tag("C")
    @DisplayName("Test no usage of split(), join(), contains(), indexOf()")
    @Test
    public void testNoUsageOfBannedMethods() {
        List<String> regexps = List.of("\\.split\\(.*?\\)", "\\.join\\(.*?\\)", "\\.contains\\(.*?\\)",
                "\\.indexOf\\(.*?\\)");
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(4)
    @Tag("C")
    @DisplayName("Test rot([], 5)")
    @Test
    public void testEmptyRotArrayList() {
        List<String> output = new ArrayList<>();
        List<String> input = new ArrayList<>();
        CaesarCipherSolver.rot(input, 5);
        assertIterableEquals(output, input);
    }

    @Order(5)
    @Tag("C")
    @DisplayName("Test rot([\"ABCD\"], 1)")
    @Test
    public void testSingleRotArrayList() {
        List<String> output = new ArrayList<>();
        output.add("BCDE");
        List<String> input = new ArrayList<>();
        input.add("ABCD");
        CaesarCipherSolver.rot(input, 1);
        assertIterableEquals(output, input);
    }

    @Order(6)
    @Tag("C")
    @DisplayName("Test rot([\"ABCD\", \"DDDD\"], 1)")
    @Test
    public void testDoubleRotArrayList() {
        List<String> output = new ArrayList<>();
        output.add("BCDE");
        output.add("EEEE");
        List<String> input = new ArrayList<>();
        input.add("ABCD");
        input.add("DDDD");
        CaesarCipherSolver.rot(input, 1);
        assertIterableEquals(output, input);
    }

    @Order(7)
    @Tag("C")
    @ParameterizedTest
    @DisplayName("Test splitBySpaces(String)")
    @ValueSource(strings = { "", "HI", "HI THERE", "A B C D E F GGG", "TRUTH IS NOT A DEMOCRACY", DECLARATION})
    public void testSplitBySpaces(String toSplit) {
        List<String> list = CaesarCipherSolver.splitBySpaces(toSplit);
        assertIterableEquals(Arrays.asList(toSplit.trim().split(" ")), list);
    }

    @Order(8)
    @Tag("C")
    @ParameterizedTest
    @DisplayName("Test putTogetherWithSpaces(String)")
    @ValueSource(strings = {"ABCD", "NEXUS", "RIVIERA"})
    public void testPutTogetherWithSpacesSingleton(String toPutTogether) {
        List<String> input = new ArrayList<>();
        input.add(toPutTogether);
        Assertions.assertEquals(toPutTogether, CaesarCipherSolver.putTogetherWithSpaces(input));
    }

    private static Stream<Arguments> putArgumentsStream() {
        return Stream.of(
                Arguments.of((Object) new String[] {"ABCD", "DDDD"}),
                Arguments.of((Object) new String[] {"ONE", "BY", "LAND", "TWO", "BY", "SEA"}),
                Arguments.of((Object) CONSTITUTION.split(" "))
        );
    }

    @Order(9)
    @Tag("C")
    @ParameterizedTest
    @DisplayName("Test putTogetherWithSpaces()")
    @MethodSource("putArgumentsStream")
    public void testPutTogetherWithSpaces(String[] strings) {
        List<String> input = Arrays.asList(strings);
        Assertions.assertEquals(String.join(" ", input), CaesarCipherSolver.putTogetherWithSpaces(input));
    }

    private static Stream<Arguments> howArgumentsStream() {
        return Stream.of(
                arguments(new String[] {""}, new String[] {"ABCD", "DDDD"}, 0),
                arguments(new String[] {"ABCD"}, new String[] {"ABCD", "DDDD"}, 1),
                arguments(new String[] {"ABCD", "EFGH"}, new String[] {"ABCD", "ABCD", "NOT"}, 2),
                // DECLARATION but with duplicate words removed
                arguments(new String[] {"WHEN", "IN", "THE", "COURSE", "OF", "HUMAN", "EVENTS", "IT", "BECOMES",
                                "NECESSARY", "FOR", "ONE", "PEOPLE", "TO", "DISSOLVE", "POLITICAL", "BANDS", "WHICH",
                                "HAVE", "CONNECTED", "THEM", "WITH", "ANOTHER", "AND", "ASSUME", "AMONG", "POWERS",
                                "EARTH", "SEPARATE", "EQUAL", "STATION", "LAWS", "NATURE", "NATURES", "GOD", "ENTITLE",
                                "A", "DECENT", "RESPECT", "OPINIONS", "MANKIND", "REQUIRES", "THAT", "THEY", "SHOULD",
                                "DECLARE", "CAUSES", "IMPEL", "SEPARATION"},
                        CONSTITUTION.split(" "), 19)
        );
    }

    @Order(10)
    @Tag("C")
    @ParameterizedTest
    @DisplayName("Test howManyWordsIn")
    @MethodSource("howArgumentsStream")
    public void testHowManyWords(String[] dict, String[] sent, int same) {
        List<String> dictionary = Arrays.asList(dict);
        List<String> sentence = Arrays.asList(sent);
        Assertions.assertEquals(same, CaesarCipherSolver.howManyWordsIn(sentence, dictionary));
    }

    private static Stream<Arguments> mainArgumentsStream() {
        return Stream.of(
                arguments("HELLO", new String[] {"HELLO"}),
                arguments("FYYFHP FY IFBS", new String[] {"ATTACK AT DAWN"}),
                arguments("AI NYUG", new String[] {"GO TEAM"}),
                arguments("NK", new String[] {"BY", "HE", "IF"}),
                arguments("EH", new String[] {"OR", "BE"}),
                arguments("BO", new String[] {"RE", "AN"}),
                // Stress tests
                arguments("DOLU PU AOL JVBYZL VM OBTHU LCLUAZ PA ILJVTLZ ULJLZZHYF MVY VUL WLVWSL AV KPZZVSCL AOL " +
                        "WVSPAPJHS IHUKZ DOPJO OHCL JVUULJALK AOLT DPAO HUVAOLY HUK AV HZZBTL HTVUN AOL WVDLYZ VM AOL " +
                        "LHYAO AOL ZLWHYHAL HUK LXBHS ZAHAPVU AV DOPJO AOL SHDZ VM UHABYL HUK VM UHABYLZ NVK LUAPASL " +
                        "AOLT H KLJLUA YLZWLJA AV AOL VWPUPVUZ VM THURPUK YLXBPYLZ AOHA AOLF ZOVBSK KLJSHYL AOL JHBZLZ " +
                        "DOPJO PTWLS AOLT AV AOL ZLWHYHAPVU", new String[] {DECLARATION}),
                arguments("RZ OCZ KZJKGZ JA OCZ PIDOZY NOVOZN DI JMYZM OJ AJMH V HJMZ KZMAZXO PIDJI ZNOVWGDNC EPNODXZ " +
                        "DINPMZ YJHZNODX OMVILPDGDOT KMJQDYZ AJM OCZ XJHHJI YZAZIXZ KMJHJOZ OCZ BZIZMVG RZGAVMZ VIY " +
                        "NZXPMZ OCZ WGZNNDIBN JA GDWZMOT OJ JPMNZGQZN VIY JPM KJNOZMDOT YJ JMYVDI VIY ZNOVWGDNC OCDN " +
                        "XJINODOPODJI AJM OCZ PIDOZY NOVOZN JA VHZMDXV", new String[] {CONSTITUTION})
        );
    }

    @Order(11)
    @Tag("C")
    @ParameterizedTest
    @DisplayName("Test main()")
    @MethodSource("mainArgumentsStream")
    public void testMain(String cipher, String[] expected) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

        byte[] input = cipher.getBytes();
        InputStream fakeIn = new ByteArrayInputStream(input);
        System.setIn(fakeIn);
        CaesarCipherSolver.main(null);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = outputStream.toString();
        System.out.print(output);
        assertEquals("Type a sentence to decrypt: " + String.join("\n", expected) +
                "\n", output.replaceAll("\r\n", "\n"));
    }
}