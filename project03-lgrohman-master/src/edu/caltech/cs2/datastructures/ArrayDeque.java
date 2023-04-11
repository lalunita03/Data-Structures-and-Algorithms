package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private int size;
    private E[] data;
    private static final int FACTOR = 2;
    private static final int CAP = 10;

    public ArrayDeque() {
        this(CAP);
    }
    public ArrayDeque(int cap) {
        this.data = (E[]) new Object[cap];
        this.size = 0;
    }

    public void addFront(E e) {

        this.resize();
        for (int i = this.size; i > 0; i--) {
            this.data[i] = this.data[i -1];
        }
        this.data[0] = e;
        this.size++;
    }

    public void addBack(E e) {
        this.resize();
        this.data[this.size] = e;
        this.size++;

    }

    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        else {
            E e = this.data[0];
            this.size--;
            for (int i = 0; i < this.size; i++) {
                this.data[i] = this.data[i + 1];
            }
            return e;
        }
    }

    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        else {
            this.size--;
            return this.data[this.size];
        }
    }

    public boolean enqueue(E e) {
        int originalSize = this.size;
        this.addFront(e);
        return this.size > originalSize;
    }

    public E dequeue() {
        return this.removeBack();
    }

    public boolean push(E e) {
        int originalSize = this.size;
        this.addBack(e);
        return this.size > originalSize;
    }

    public E pop() {
        return this.removeBack();
    }

    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        else {
            return this.data[0];
        }
    }

    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        else {
            return this.data[this.size - 1];
        }
    }

    public E peek() {
        return this.peekBack();
    }

    public Iterator<E> iterator() {
        return new ArraySetIterator();
    }

    public int size() {
        return this.size;
    }
    public String toString() {
        if (size() == 0) {
            return "[]";
        }

        String result = "[";
        for (int i = 0; i < size(); i++) {
            result += this.data[i] + ", ";
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";
    }
    private void resize() {
        if (this.size >= this.data.length) {
            E[] newData = (E[]) new Object[(int)(this.data.length * FACTOR)];

            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }

            this.data = newData;
        }
    }
    private class ArraySetIterator implements Iterator<E> {
        private int idx;

        public ArraySetIterator() {
            this.idx = 0;
        }
        public boolean hasNext() {
            return this.idx < size();
        }
        public E next() {
            E toReturn = data[this.idx];
            this.idx++;
            return toReturn;
        }
    }
}

