package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {
    private IFixedSizeQueue<Double> q;
    private static final int RATE = 44100;
    private static final double DECAY = 0.996;
    private static Random rand = new Random();
    public CircularArrayFixedSizeQueueGuitarString(double frequency) {
        this.q = new CircularArrayFixedSizeQueue<>((int) Math.ceil((RATE / frequency)));

        for (int i = 0; i < q.capacity(); i++) {
            q.add(0.0);
        }
    }

    public int length() {
        return q.size();
    }

    public void pluck() {
        q.clear();
        for (int i = 0; i < q.capacity(); i++) {
            q.add(rand.nextDouble(-0.5, 0.5));
        }
    }

    public void tic() {
        double value = (q.dequeue() + q.peek()) * DECAY / 2;
        q.enqueue(value);
    }

    public double sample() {
        return q.peek();
    }
}
