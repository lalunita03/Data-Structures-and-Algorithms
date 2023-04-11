package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private static class Node<K, V> {
        public final K key;
        public V value;
        public Node<K, V> next;
        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V> head;
    private int size;

    public MoveToFrontDictionary() {
        this.size = 0;
        this.head = null;
    }

    @Override
    public V remove(K key) {
        V value = this.get(key);
        if (value != null) {
            this.head = this.head.next;
            this.size--;
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        V old = this.get(key);

        if (old == null) {
            Node<K,V> current = new Node<>(key, value, this.head);
            this.head = current;
            this.size++;
        }
        else {
            this.head.value = value;
        }
        return old;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> keys = new LinkedDeque<>();
        if (this.head != null) {
            Node<K,V> current = this.head;

            for (int i = 0; i < this.size; i++) {
                keys.add(current.key);
                current = current.next;
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> vals = new LinkedDeque<>();
        if (this.head != null) {
            Node<K,V> current = this.head;

            for (int i = 0; i < this.size; i++) {
                vals.add(current.value);
                current = current.next;
            }
        }
        return vals;
    }

    public V get(K key) {
        if (this.head == null) {
            return null;
        }

        if (this.head.key.equals(key)) {
            return this.head.value;
        }

        Node<K,V> current = this.head;
        Node<K,V> temp = null;

        for (int i = 0; i < this.size; i++) {

            if (current.key.equals(key)) {
                temp.next = current.next;
                current.next = this.head;
                this.head = current;

                return current.value;
            }

            temp = current;
            current = current.next;
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new ArraySetIterator();
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }

        String result = "[";
        Node<K,V> current = this.head;
        for (int i = 0; i < this.size; i++) {
            result += current.key + ", ";
            current = current.next;
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";    }

    private class ArraySetIterator implements Iterator<K> {
        private Node<K, V> current;
        private int idx;

        public ArraySetIterator() {
            this.current = head;
            this.idx = 0;

        }
        public boolean hasNext() {
            return this.idx < size();
        }
        public K next() {
            K toReturn = current.key;
            current = current.next;
            this.idx++;
            return toReturn;
        }
    }
}
