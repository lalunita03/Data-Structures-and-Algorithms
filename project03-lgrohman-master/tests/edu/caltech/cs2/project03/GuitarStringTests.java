package edu.caltech.cs2.project03;

import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.TestDescription;
import edu.caltech.cs2.helpers.TestExtension;
import edu.caltech.cs2.helpers.TestHint;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;
import edu.caltech.cs2.interfaces.IStyleTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("A")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
public class GuitarStringTests {
  private static String STRING_SOURCE = "src/edu/caltech/cs2/project03/CircularArrayFixedSizeQueueGuitarString.java";

  public static CircularArrayFixedSizeQueueGuitarString constructGuitarString(double frequency) {
    Constructor c = Reflection.getConstructor(CircularArrayFixedSizeQueueGuitarString.class, double.class);
    return Reflection.newInstance(c, frequency);
  }

  public static IFixedSizeQueue<Double> getQueueFromString(CircularArrayFixedSizeQueueGuitarString string) {
    String queueName = Reflection.getFieldByType(CircularArrayFixedSizeQueueGuitarString.class, IFixedSizeQueue.class)
            .getName();
    return Reflection.getFieldValue(CircularArrayFixedSizeQueueGuitarString.class, queueName, string);
  }
  @DisplayName("Style")
  @Nested
  class StyleTests implements IStyleTests {

    @Order(classSpecificTestLevel)
    @DisplayName("There are three static fields: the two double constants and a random value generator")
    @TestDescription("This test is checking that you have three constant fields for the \"magic numbers\" and the Random instance.")
    @Test
    public void testStaticFields() {
      Reflection.assertFieldsEqualTo(CircularArrayFixedSizeQueueGuitarString.class, "static", 3);
      Stream<Field> fields = Reflection.getFields(CircularArrayFixedSizeQueueGuitarString.class);
      fields.filter(Reflection.hasModifier("static")).forEach((field) -> {
        Reflection.checkFieldModifiers(field, List.of("private", "static"));
        assertTrue(Reflection.hasModifier("final").test(field) || field.getType().equals(Random.class),
                "non-final static class must be a random value generator");
      });
    }

    public int getMaxFields() {
      return 3;
    }

    @Override
    public List<String> methodsToBanSelf() {
      return List.of();
    }

    public List<String> getPublicInterface() {
      return List.of("length", "pluck", "tic", "sample");
    }

    @Override
    public String getSource() {
      return STRING_SOURCE;
    }

    @Override
    public Class<?> getClazz() {
      return CircularArrayFixedSizeQueueGuitarString.class;
    }
  }

  @DisplayName("Implementation Tests")
  @Nested
  class ImplementationTests {
    @Order(classSpecificTestLevel)
    @DisplayName("The constructor correctly sets up the queue")
    @TestHint("Make sure that your queue is the correct size according to the specification and filled with zeroes.")
    @ParameterizedTest(name = "Test constructor with CircularArrayFixedSizeQueue and a frequency of {0} Hz; expected queue size is {1}")
    @CsvSource({"110, 401", "340, 130", "512, 87", "600.5, 74", "880, 51"})
    public void testConstructor(double frequency, int expectedSize) {
      CircularArrayFixedSizeQueueGuitarString string = constructGuitarString(frequency);
      IFixedSizeQueue<Double> queue = getQueueFromString(string);

      assertEquals(expectedSize, queue.size(), "Queue size is not equal to expected size");
      for (double val : queue) {
        assertEquals(0, val, "All values in queue should be equal to 0");
      }
    }

    @Order(guitarStringTestLevel)
    @DisplayName("The pluck() method randomizes the values in the queue")
    @ParameterizedTest(name = "Test pluck() with CircularArrayFixedSizeQueue and a frequency of {0} Hz")
    @TestHint("Make sure that you're centering your distribution around the correct number.")
    @CsvSource({"100", "50", "10", "8", "5"})
    public void testPluck(double frequency) {
      final double DELTA = 0.05;
      // Set up class and retrieve queue
      CircularArrayFixedSizeQueueGuitarString string = constructGuitarString(frequency);
      IFixedSizeQueue<Double> queue = getQueueFromString(string);
      // Checks all values are initially 0
      for (double val : queue) {
        assertEquals(0, val, "initial values must be 0");
      }
      string.pluck();
      queue = getQueueFromString(string);
      double sum = 0;
      double absSum = 0;
      for (double val : queue) {
        sum += val;
        absSum += abs(val);
      }
      assertEquals(0, sum / queue.size(), DELTA, "average value of uniform distribution should be near 0");
      assertEquals(0.25, absSum / queue.size(), DELTA, "average magnitude of uniform distribution should be near 0.25");
    }

    @Order(guitarStringTestLevel)
    @DisplayName("The tic() method correctly applies the Karplus-Strong algorithm")
    @ParameterizedTest(name = "Test tic() with CircularArrayFixedSizeQueue and a frequency of {0} Hz; data file {1}.txt")
    @CsvSource({"10000, ticStates1", "8000, ticStates2", "5000, ticStates3"})
    public void testTic(double frequency, String filename) {
      // Set up scanner
      String filepath = "tests/data/" + filename + ".txt";
      Scanner in;
      try {
        in = new Scanner(new File(filepath));
      } catch (FileNotFoundException e) {
        throw new IllegalArgumentException(filepath + " is not a valid trace file.");
      }
      // Set up class and retrieve queue
      CircularArrayFixedSizeQueueGuitarString string = constructGuitarString(frequency);
      IFixedSizeQueue<Double> queue = getQueueFromString(string);
      // Reinitialize queue with new data
      for (int i = 0; i < queue.size(); i++) {
        queue.dequeue();
        queue.enqueue(in.nextDouble());
      }
      int initSize = queue.size();
      // Pass through the same number of tics as elements in the array
      for (int i = 0; i < initSize; i++) {
        string.tic();
        queue = getQueueFromString(string);
        assertEquals(initSize, queue.size(), "queue size must remain the same");
      }
      // Compare peek() values with the expected values in the files
      while (in.hasNext()) {
        string.tic();
        queue = getQueueFromString(string);
        assertEquals(initSize, queue.size(), "queue size must remain the same");
        assertEquals(in.nextDouble(), queue.peek(), "next expected value not at front of queue");
      }
    }

    @Order(guitarStringTestLevel)
    @DisplayName("The length() method correctly gives the length of the queue")
    @ParameterizedTest(name = "Test length() with CircularArrayFixedSizeQueue and a frequency of {0} Hz; expected length = {1}; iterations = {2}")
    @CsvSource({"110, 401, 1000", "340, 130, 500", "512, 87, 200", "600.5, 74, 150", "880, 51, 100"})
    public void testLength(double frequency, int expectedLength, int iterations) {
      // Set up class and retrieve queue
      CircularArrayFixedSizeQueueGuitarString string = constructGuitarString(frequency);
      IFixedSizeQueue<Double> queue = getQueueFromString(string);

      // Pluck and make sure length doesn't change
      int initSize = queue.size();
      assertEquals(expectedLength, string.length(), "Length should be same as expected");
      assertEquals(queue.size(), string.length(), "Length should be same as queue size");
      string.pluck();
      queue = getQueueFromString(string);
      assertEquals(initSize, string.length(), "Length should not have changed from beginning");
      assertEquals(queue.size(), string.length(), "Length should be same as queue size");

      // Run through many iterations, making sure both the queue size and length are
      // constant
      for (int i = 0; i < iterations; i++) {
        string.tic();
        queue = getQueueFromString(string);
        assertEquals(initSize, string.length(), "Length should not have changed from beginning");
        assertEquals(queue.size(), string.length(), "Length should be same as queue size");
      }

    }

    @Order(guitarStringTestLevel)
    @DisplayName("The sample() method gives the same values as peek()ing the queue")
    @ParameterizedTest(name = "Test sample() with CircularArrayFixedSizeQueue and a frequency of {0} Hz")
    @CsvSource({"110, 1000", "340, 500", "512, 200", "600.5, 150", "880, 100"})
    public void testSample(double frequency, int iterations) {
      // Set up class and retrieve queue
      CircularArrayFixedSizeQueueGuitarString string = constructGuitarString(frequency);
      IFixedSizeQueue<Double> queue = getQueueFromString(string);

      // Pluck and make sure initial samples are correct
      assertEquals(0, string.sample(), "Sample should return 0 before plucking");
      assertEquals(queue.peek(), string.sample(), "Sample should same as peek()ing queue");
      string.pluck();
      queue = getQueueFromString(string);
      assertEquals(queue.peek(), string.sample(), "Sample should same as peek()ing queue");

      // Run through many iterations, making sure sample() matches peek()
      for (int i = 0; i < iterations; i++) {
        string.tic();
        queue = getQueueFromString(string);
        assertEquals(queue.peek(), string.sample(), "Sample should same as peek()ing queue");
      }

    }
  }

}
