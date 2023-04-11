package edu.caltech.cs2.project01;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubstitutionCipherSolverTests {

    static Stream<Arguments> ciphertextArgumentsProvider() {
        return Stream.of(Arguments.of(
                "ILBNUPQCNYBNBGIYGDYDZNZUPGIBGQZIBPGPTUNOYTPUQNPGZXNICZUIBPGBGACPVCZEEBGVXPILBGDYNBVGPTACPVCZ" +
                        "ENZGDDZIZNIPCZVYOYDBNUQNNZGDBEASYEYGITQGDZEYGIZSDZIZNICQUIQCYNZGDZSVPCBILENRBZZNYCBY" +
                        "NPTSZXNZGDACPFYUINOYOBSSVCZDYPGUPCCYUIGYNNZGDYTTBUBYGUJPTPQCACPVCZENILBNUPQCNYQNYNFZ" +
                        "RZZNZGBEASYEYGIZIBPGSZGVQZVYXQIOYDPGPIYKAYUIZGJACBPCFZRZACPVCZEEBGVYKAYCBYGUY",
                "THISCOURSEISINTENDEDASACONTINUATIONOFCSWEFOCUSONABSTRACTIONINPROGRAMMINGBOTHINDESIGNOFPROGRA" +
                        "MSANDDATASTORAGEWEDISCUSSANDIMPLEMENTFUNDAMENTALDATASTRUCTURESANDALGORITHMSVIAASERIE" +
                        "SOFLABSANDPROJECTSWEWILLGRADEONCORRECTNESSANDEFFICIENCYOFOURPROGRAMSTHISCOURSEUSESJA" +
                        "VAASANIMPLEMENTATIONLANGUAGEBUTWEDONOTEXPECTANYPRIORJAVAPROGRAMMINGEXPERIENCE"
                ),
                Arguments.of(
                        "BIKQIHIEHYIERKQIVUGKIXDKFKIDGUEAXIAKEREATFTEAIHIARILKVUGEUIDKFSYGDQMVDKGLIGUDVAIXETIDKG" +
                                "LKAFUZVGYGKPHAEWGXIREAKQILETTEUXIRIULIHAETEKIKQIJIUIAFYBIYRFAIFUXDILVAIKQISYIDD" +
                                "GUJDERYGSIAKPKEEVADIYWIDFUXEVAHEDKIAGKPXEEAXFGUFUXIDKFSYGDQKQGDLEUDKGKVKGEUREAK" +
                                "QIVUGKIXDKFKIDERFTIAGLF",
                        "WETHEPEOPLEOFTHEUNITEDSTATESINORDERTOFORMAMOREPERFECTUNIONESTABLISHJUSTICEINSUREDOMESTI" +
                                "CTRANQUILITYPROVIDEFORTHECOMMONDEFENCEPROMOTETHEGENERALWELFAREANDSECURETHEBLESS" +
                                "INGSOFLIBERTYTOOURSELVESANDOURPOSTERITYDOORDAINANDESTABLISHTHISCONSTITUTIONFORT" +
                                "HEUNITEDSTATESOFAMERICA"
                ),
                Arguments.of(
                        "SOWFBRKAWFCZFSBSCSBQITBKOWLBFXTBKOWLSOXSOXFZWWIBICFWUQLRXINOCIJLWJFQUNWXLFBSZXFBTXAANTQ" +
                                "IFBFSFQUFCZFSBSCSBIMWHWLNKAXBISWGSTOXLXTSWLUQLXJBUUWLWISTBKOWLSWGSTOXLXTSWLBSJB" +
                                "UUWLFULQRTXWFXLTBKOWLBISOXSSOWTBKOWLXAKOXZWSBFIQSFBRKANSOWXAKOXZWSFOBUSWJBSBFTQ" +
                                "RKAWSWANECRZAWJ",
                        "THESIMPLESUBSTITUTIONCIPHERISACIPHERTHATHASBEENINUSEFORMANYHUNDREDSOFYEARSITBASICALLYCO" +
                                "NSISTSOFSUBSTITUTINGEVERYPLAINTEXTCHARACTERFORADIFFERENTCIPHERTEXTCHARACTERITDI" +
                                "FFERSFROMCAESARCIPHERINTHATTHECIPHERALPHABETISNOTSIMPLYTHEALPHABETSHIFTEDITISCO" +
                                "MPLETELYJUMBLED"
                ));
    }
    @Order(0)
    @Tag("B")
    @ParameterizedTest(name = "Test with ciphertext = {0}")
    @DisplayName("Test main()")
    @MethodSource("ciphertextArgumentsProvider")
    public void testSubstitutionCipherMain(String ciphertext, String expected) throws FileNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        byte[] input = ciphertext.getBytes();
        InputStream fakeIn = new ByteArrayInputStream(input);
        System.setIn(fakeIn);
        SubstitutionCipherSolver.main(null);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = outputStream.toString();
        System.out.print(output);
        assertEquals("Type a sentence to decrypt: " + expected + "\n", output.replaceAll("\r\n", "\n"));
    }
}
