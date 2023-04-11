package edu.caltech.cs2.project01;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class SubstitutionCipherSolver {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Type a sentence to decrypt: ");
        String ciphertext = scan.nextLine();

        QuadGramLikelihoods likelihoods = new QuadGramLikelihoods();
        SubstitutionCipher best = new SubstitutionCipher(ciphertext);
        for (int i = 0; i < 20; i ++) {
            SubstitutionCipher cipher = best.getSolution(likelihoods);
            if (cipher.getScore(likelihoods) > best.getScore(likelihoods)) {
                best = cipher;
            }
        }
        System.out.println(best.getPlainText());
    }
}