package edu.caltech.cs2.project01;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubstitutionCipher {
    private String ciphertext;
    private Map<Character, Character> key;

    // Use this Random object to generate random numbers in your code,
    // but do not modify this line.
    private static final Random RANDOM = new Random();

    /**
     * Construct a SubstitutionCipher with the given cipher text and key
     * @param ciphertext the cipher text for this substitution cipher
     * @param key the map from cipher text characters to plaintext characters
     */
    public SubstitutionCipher(String ciphertext, Map<Character, Character> key) {
        this.ciphertext = ciphertext;
        this.key = key;
    }

    /**
     * Construct a SubstitutionCipher with the given cipher text and a randomly
     * initialized key.
     * @param ciphertext the cipher text for this substitution cipher
     */
    public SubstitutionCipher(String ciphertext) {
        this.ciphertext = ciphertext;
        this.key = new HashMap<Character, Character>();

        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            this.key.put(letter, letter);
        }
        for (int i = 0; i < 10000; i++){
            SubstitutionCipher sc = randomSwap();
            this.key = sc.key;
        }
    }

    /**
     * Returns the unedited cipher text that was provided by the user.
     * @return the cipher text for this substitution cipher
     */
    public String getCipherText() {
        return this.ciphertext;
    }

    /**
     * Applies this cipher's key onto this cipher's text.
     * That is, each letter should be replaced with whichever
     * letter it maps to in this cipher's key.
     * @return the resulting plain text after the transformation using the key
     */
    public String getPlainText() {
        String text = "";
        for (int i = 0; i < this.ciphertext.length(); i++) {
            char c = this.ciphertext.charAt(i);
            text += Character.toString(this.key.get(c));
        }
        return text;
    }

    /**
     * Returns a new SubstitutionCipher with the same cipher text as this one
     * and a modified key with exactly one random pair of characters exchanged.
     *
     * @return the new SubstitutionCipher
     */
    public SubstitutionCipher randomSwap() {
        Map<Character, Character> newKey = new HashMap<>(this.key);
        char a = (char) ('A' + RANDOM.nextInt(newKey.size()));
        char b = (char) ('A' + RANDOM.nextInt(newKey.size()));
        while (a == b) {
            b = (char) ('A' + RANDOM.nextInt(newKey.size()));
        }

        char aValue = newKey.get(a);
        char bValue = newKey.get(b);

        newKey.replace(a, bValue);
        newKey.replace(b, aValue);

        return new SubstitutionCipher(this.ciphertext, newKey);
    }

    /**
     * Returns the "score" for the "plain text" for this cipher.
     * The score for each individual quadgram is calculated by
     * the provided likelihoods object. The total score for the text is just
     * the sum of these scores.
     * @param likelihoods the object used to find a score for a quadgram
     * @return the score of the plain text as calculated by likelihoods
     */
    public double getScore(QuadGramLikelihoods likelihoods) {
        String text = this.getPlainText();
        double score = 0.0;
        for (int i = 0; i <= text.length() - 4; i++) {
            String quad = text.substring(i, i + 4);
            score += likelihoods.get(quad);
        }
        return score;
    }

    /**
     * Attempt to solve this substitution cipher through the hill
     * climbing algorithm. The SubstitutionCipher this is called from
     * should not be modified.
     * @param likelihoods the object used to find a score for a quadgram
     * @return a SubstitutionCipher with the same ciphertext and the optimal
     *  found through hill climbing
     */
    public SubstitutionCipher getSolution(QuadGramLikelihoods likelihoods) {
        SubstitutionCipher sc = new SubstitutionCipher(this.ciphertext);
        int trials = 0;
        while (trials < 1000) {
            SubstitutionCipher newSC = sc.randomSwap();
            if (newSC.getScore(likelihoods) > sc.getScore(likelihoods)) {
                sc = newSC;
                trials = 0;

            }
            else {
                trials += 1;
            }
        }
        return sc;
    }
}
