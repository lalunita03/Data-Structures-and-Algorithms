package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;
import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private Node<E> head;
    private Node<E> tail;

    private int size;
    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;


        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> next) {
            this(data, next, null);
        }

        public Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;

        }
    }

    public LinkedDeque() {
        this.head = new Node<>(null);
        this.tail = this.head;
        this.size = 0;
    }

    @Override
    public void addFront(E e) {
        if (this.size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
        }
        else {
            this.head.prev = new Node<>(e, this.head);
            this.head = this.head.prev;
        }
        this.size++;
    }

    @Override
    public void addBack(E e) {
        if (size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
        }
        else {
            this.tail.next = new Node<>(e, null, this.tail);
            this.tail = this.tail.next;
        }
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size < 1) {
            return null;
        }
        E first = this.head.data;
        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        else {
            this.head = this.head.next;
            this.head.prev = null;

        }
        this.size--;
        return first;
    }

    @Override
    public E removeBack() {
        if (this.size < 1) {
            return null;
        }
        E last = this.tail.data;

        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        else {
            this.tail = this.tail.prev;
            this.tail.next = null;
        }
        this.size--;
        return last;
    }

    @Override
    public boolean enqueue(E e) {
        int originalSize = this.size;
        this.addFront(e);
        return this.size > originalSize;    }

    @Override
    public E dequeue() {
        return this.removeBack();    }

    @Override
    public boolean push(E e) {
        int originalSize = this.size;
        this.addBack(e);
        return this.size > originalSize;    }

    @Override
    public E pop() {
        return this.removeBack();    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        else {
            return this.head.data;
        }    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        else {
            return this.tail.data;
        }    }

    @Override
    public E peek() {
        return this.peekBack();    }

    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator();    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }

        String result = "[";
        Node<E> current = this.head;
        for (int i = 0; i < this.size; i++) {
            result += current.data + ", ";
            current = current.next;
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";    }

    private class ArraySetIterator implements Iterator<E> {
        private Node<E> current;
        private int idx;

        public ArraySetIterator() {
            this.current = head;
            this.idx = 0;

        }
        public boolean hasNext() {
            return this.idx < size();
        }
        public E next() {
            E toReturn = current.data;
            current = current.next;
            this.idx++;
            return toReturn;
        }
    }
}
