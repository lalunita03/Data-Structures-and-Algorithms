package edu.caltech.cs2.project02;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import edu.caltech.cs2.project02.guessers.ConsoleHangmanGuesser;
import edu.caltech.cs2.project02.choosers.RandomHangmanChooser;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class HangmanGame {
  public static void main(String[] args) throws FileNotFoundException {
    System.out.println("Welcome to the cs2 console hangman game.");
    System.out.println();

    // set basic parameters
    Scanner console = new Scanner(System.in);
    System.out.print("What length word do you want to use? ");
    int wordLength = console.nextInt();
    System.out.print("How many wrong answers allowed? ");
    int maxGuesses = console.nextInt();
    System.out.println();

    // set up the the hangman chooser and start the game
    IHangmanChooser chooser = new RandomHangmanChooser(wordLength, maxGuesses);
    IHangmanGuesser guesser = new ConsoleHangmanGuesser(console);
    playGame(chooser, guesser);
    showResults(chooser);
  }

  // Plays one game with the user
  public static void playGame(IHangmanChooser chooser, IHangmanGuesser guesser) throws FileNotFoundException {
    while (!chooser.isGameOver()) {
      String pattern = chooser.getPattern();
      Set<Character> guesses = chooser.getGuesses();
      System.out.println("guesses : " + chooser.getGuessesRemaining());
      System.out.println("guessed : " + guesses);
      System.out.println("current : " + pattern);
      char guess = 'a';
      int count = -1;
      while (count < 0) {
        guess = guesser.getGuess(pattern, guesses);
        try {
          count = chooser.makeGuess(guess);
        } catch (IllegalArgumentException e) {
          System.out.println("'" + guess + "' wasn't a valid guess because " + e.getMessage() + ".");
        }
      }

      if (count == 0) {
        System.out.println("Sorry, there are no " + guess + "'s");
      } else if (count == 1) {
        System.out.println("Yes, there is one " + guess);
      } else {
        System.out.println("Yes, there are " + count + " " + guess + "'s");
      }
      System.out.println();
    }
  }

  // reports the results of the game, including showing the answer
  public static void showResults(IHangmanChooser hangman) {
    if (hangman.getGuessesRemaining() > 0) {
      System.out.println("That was my word! You beat me!");
    } else {
      System.out.println("Sorry, you lose!");
      System.out.println("My word was '" + hangman.getWord() + "'.");
    }
  }
}