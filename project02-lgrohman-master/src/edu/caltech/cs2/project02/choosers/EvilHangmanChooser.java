package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {
  private static int guesses;
  private static SortedSet<Character> letters;
  private static SortedSet<String> secretWords;
  private static String currentPattern;
  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    if (wordLength < 1 || maxGuesses < 1){
      throw new IllegalArgumentException();
    }

    secretWords = new TreeSet<String>();
    Scanner scanner = new Scanner(new File("data/scrabble.txt"));
    while (scanner.hasNextLine()) {
      String word = scanner.nextLine();
      if (wordLength == word.length()) {
        this.secretWords.add(word);
      }
    }

    if (this.secretWords.size() == 0) {
      throw new IllegalStateException();
    }

    this.currentPattern = "";
    for (int i = 0; i < wordLength; i++) {
      this.currentPattern += "-";
    }

    this.guesses = maxGuesses;
    this.letters = new TreeSet<Character>();
  }

  @Override
  public int makeGuess(char letter) {
    if (this.guesses < 1) {
      throw new IllegalStateException();
    }
    if (!Character.isLowerCase(letter)) {
      throw new IllegalArgumentException();
    }

    for (char c : letters) {
      if (c == letter) {
        throw new IllegalArgumentException();
      }
    }

    for (int i = 0; i < this.secretWords.size(); i++) {

    }

    int occurances = 0;
    this.letters.add(letter);

    TreeMap<String, SortedSet<String>> tree = new TreeMap<String, SortedSet<String>>();

    for (String word : this.secretWords) {
      String pattern = "";

      for (int i = 0; i < word.length(); i++) {
        if (this.letters.contains(word.charAt(i))) {
          pattern += Character.toString(word.charAt(i));
        }
        else {
          pattern += "-";
        }
      }

      SortedSet<String> family = new TreeSet<String>();

      if (tree.containsKey(pattern)) {
        family = tree.get(pattern);
        family.add(word);
        tree.replace(pattern, family);
      }
      else {
        family.add(word);
        tree.put(pattern, family);
      }
    }

    int maxSize = 0;

    for (String pattern : tree.keySet()) {
      if (maxSize < tree.get(pattern).size()) {
        maxSize = tree.get(pattern).size();
        this.secretWords = tree.get(pattern);
        this.currentPattern = pattern;
      }
    }

    for (int i = 0; i < this.currentPattern.length(); i++) {
      if (this.currentPattern.charAt(i) == letter) {
        occurances++;
      }
    }

    if (occurances == 0) {
      this.guesses--;
    }

    return occurances;  }

  @Override
  public boolean isGameOver() {
    if (this.guesses == 0) {
      return true;
    }

    if (this.secretWords.size() > 1) {
      return false;
    }
    else {
      String word = this.secretWords.first();

      for (int i = 0; i < word.length(); i++) {
        if (!letters.contains(word.charAt(i))) {
          return false;
        }
      }
    }
    return true;  }

  @Override
  public String getPattern() {
    return this.currentPattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.letters;
  }

  @Override
  public int getGuessesRemaining() {
    return this.guesses;
  }

  @Override
  public String getWord() {
    this.guesses = 0;
    return this.secretWords.first();
  }
}