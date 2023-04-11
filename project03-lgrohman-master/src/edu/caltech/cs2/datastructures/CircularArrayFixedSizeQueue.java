package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Iterator;

public class CircularArrayFixedSizeQueue<E> implements IFixedSizeQueue<E> {
    private E[] data;
    private int size;
    private int front;
    public CircularArrayFixedSizeQueue(int capacity) {
        this.data = (E[]) new Object[capacity];
        this.size = 0;
        this.front = 0;
    }

    @Override
    public boolean isFull() {
        return this.size >= this.data.length;
    }

    @Override
    public int capacity() {
        return this.data.length;
    }

    @Override
    public boolean enqueue(E e) {
        if (isFull()) {
            return false;
        }
        this.data[(this.front + this.size) % this.data.length] = e;
        this.size++;
        return true;
    }

    @Override
    public E dequeue() {
        if (size == 0) {
            return null;
        }
        E first = this.data[this.front];
        this.front = (this.front + 1) % this.data.length;
        this.size--;
        return first;
    }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }
        return this.data[this.front];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void add(E e) {
        this.enqueue(e);

    }

    @Override
    public void clear() {
        this.size = 0;
        this.front = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator();
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

    private class ArraySetIterator implements Iterator<E> {
        private int idx;

        public ArraySetIterator() {
            this.idx = 0;
        }
        public boolean hasNext() {
            return this.idx < size;
        }
        public E next() {
            E toReturn = data[(front + this.idx) % data.length];
            this.idx++;
            return toReturn;
        }
    }
}

