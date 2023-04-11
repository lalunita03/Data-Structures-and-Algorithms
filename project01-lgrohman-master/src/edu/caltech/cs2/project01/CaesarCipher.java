package edu.caltech.cs2.project01;

import java.util.Scanner;

public class CaesarCipher {
    public static final char[] ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * Returns the 0-based index in the ALPHABET array where c appears,
     * or -1 if c is not in ALPHABET
     * For example:
     *  findIndexInAlphabet('A') returns 0
     *  findIndexInAlphabet('D') returns 3
     *  findIndexInAlphabet('+') returns -1
     *  findIndexInAlphabet('a') returns -1
     * @param c the character whose index should be returned
     * @return the index of c in ALPHABET or -1 if c is not in ALPHABET
     */
    public static int findIndexInAlphabet(char c) {
        for (int i = 0; i < ALPHABET.length; i++) {
            if (ALPHABET[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns c rotated by amount many characters. If we run out of letters
     * in the alphabet, rotate back to 'A'. You may find it useful to call
     * your findIndexInAlphabet method when writing this one.
     *
     * For example:
     *  rot('A', 0) returns 'A'
     *  rot('A', 1) returns 'B'
     *  rot('Z', 1) returns 'A'
     *  rot('A', 10) returns 'K'
     *  rot('J', 25) returns 'I'
     *
     * @param c the character to rotate
     * @param amount the number of characters to rotate by
     * @return the rotated version of the character
     */
    public static char rot(char c, int amount) {
        int i = findIndexInAlphabet(c);
        int j = (i + amount) % (ALPHABET.length);
        return ALPHABET[j];
    }

    /**
     * Returns a new string in which every character has been rotated
     * by amount letters. This method assumes that every character in line is a
     * upper-case alphabetic character. You may find it useful to call
     * your rot character method when writing this one.
     *
     * For example:
     *  rot("A", 0) returns "A"
     *  rot("AA", 3) returns "DD"
     *  rot("HELLO", 0) returns "HELLO"
     *  rot("HELLO", 1) returns "IFMMP"
     *
     * @param line the string whose characters we want to rotate
     * @param amount the number of characters to rotate each character in line by
     * @return the rotated string
     */
    public static String rot(String line, int amount) {
        String result = "";
        for (char c : line.toCharArray()) {
            result += Character.toString(rot(c, amount));
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Type text to encrypt: ");
        String line = in.nextLine();
        System.out.print("Type a number to rotate by: ");
        int amount = in.nextInt();

        System.out.println(rot(line, amount));
    }
}
