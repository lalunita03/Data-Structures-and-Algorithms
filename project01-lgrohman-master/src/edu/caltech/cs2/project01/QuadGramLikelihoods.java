package edu.caltech.cs2.project01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class QuadGramLikelihoods {
    private Map<String, Double> likelihoods;
    private double notFoundLikelihood;

    public QuadGramLikelihoods() throws FileNotFoundException {
        Map<String, Integer> freqs = getQuadFrequencies();

        long total = freqs.size();
        for (int freq : freqs.values()) {
            total += freq;
        }

        this.notFoundLikelihood = -Math.log10(total);
        this.likelihoods = new HashMap<>();
        for (Map.Entry<String, Integer> qg : freqs.entrySet()) {
            this.likelihoods.put(qg.getKey(), Math.log10(qg.getValue()) + this.notFoundLikelihood);
        }
    }

    public static Map<String, Integer> getQuadFrequencies() throws FileNotFoundException {
        Map<String, Integer> freqs = new HashMap<>();
        try (Scanner quadgrams = new Scanner(new File("english_quadgrams.txt"))) {
            while (quadgrams.hasNext()) {
                freqs.put(quadgrams.next(), quadgrams.nextInt());
            }
        }
        return freqs;
    }

    /**
     * Returns the log-likelihood of the provided quad-gram.
     * @param quadgram the quad-gram to get the likelihood of
     * @return the log-likelihood as calculated by our data set
     */
    public double get(String quadgram) {
        return this.likelihoods.getOrDefault(quadgram, this.notFoundLikelihood);
    }
}
