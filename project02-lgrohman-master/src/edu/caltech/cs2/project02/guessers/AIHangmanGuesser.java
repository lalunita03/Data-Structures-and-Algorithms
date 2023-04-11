package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {

  private static final String filename = "data/scrabble.txt";
  @Override
  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {

    Scanner scanner = new Scanner(new File(this.filename));
    SortedSet<String> words = new TreeSet<String>();

// Step 1
    while (scanner.hasNextLine()) {
      String word = scanner.nextLine();
      if (pattern.length() == word.length()) {

        Boolean match = true;
        for (int i = 0; i < word.length(); i++) {
          // ad-m, adam
          if (pattern.charAt(i) == '-' && guesses.contains(word.charAt(i))) {
            match = false;
          }
          else if (pattern.charAt(i) != '-' && pattern.charAt(i) != word.charAt(i)) {
            match = false;
          }
        }

        if(match) {
          words.add(word);
        }
      }
    }

// Step 2
    Set<Character> unguessed = new TreeSet<Character>();
    TreeMap<Character, Integer> occurences = new TreeMap<Character, Integer>();

    for (int i = 'a'; i <= 'z'; i++) {
      if(!guesses.contains((char) i)) {
        unguessed.add((char) i);
      }
    }

    for (char c : unguessed) {
      occurences.put(c, 0);

      for (String word : words) {
        for (int i = 0; i < word.length(); i++) {
          if (c == word.charAt(i)) {
            occurences.replace(c, occurences.get(c) + 1);
          }
        }
      }
    }

    int max = 0;
    char letter = 'a';
    for(char c : unguessed) {
      if(occurences.get(c) > max) {
        max = occurences.get(c);
        letter = c;
      }
    }
    return letter;
  }
}
