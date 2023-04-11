package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RandomHangmanChooser implements IHangmanChooser {

  private final static Random rand = new Random();
  private static int guesses;
  private static SortedSet<Character> letters;
  private final String secretWord;

  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    if (wordLength < 1 || maxGuesses < 1){
      throw new IllegalArgumentException();
    }

    Scanner scanner = new Scanner(new File("data/scrabble.txt"));
    SortedSet<String> words = new TreeSet<String>();

    while (scanner.hasNextLine()) {
      String word = scanner.nextLine();
      if (wordLength == word.length()) {
        words.add(word);
      }
    }

    if (words.size() == 0) {
      throw new IllegalStateException();
    }

    int i = rand.nextInt(words.size());

    this.guesses = maxGuesses;
    this.secretWord = new ArrayList<String>(words).get(i);
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

    int occurances = 0;
    this.letters.add(letter);

    for (int i = 0; i < this.secretWord.length(); i++) {
      if (this.secretWord.charAt(i) == letter) {
        occurances++;
      }
    }

    if (occurances == 0) {
      this.guesses--;
    }

    return occurances;
  }

  @Override
  public boolean isGameOver() {
    if (this.guesses == 0) {
      return true;
    }

    for (int i = 0; i < this.secretWord.length(); i++) {
      if (!letters.contains(this.secretWord.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public String getPattern() {
    String output = "";

    for (int i = 0; i < this.secretWord.length(); i++) {
      if (letters.contains(this.secretWord.charAt(i))) {
        output += Character.toString(this.secretWord.charAt(i));
      }
      else {
        output += "-";
      }
    }

    return output;
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
    return this.secretWord;
  }
}