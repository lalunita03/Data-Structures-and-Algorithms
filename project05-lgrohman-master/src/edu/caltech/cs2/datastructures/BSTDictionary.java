package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class BSTDictionary<K extends Comparable<? super K>, V>
        implements IDictionary<K, V> {

    private BSTNode<K, V> root;
    private int size;

    /**
     * Class representing an individual node in the Binary Search Tree
     */
    private static class BSTNode<K, V> {
        public final K key;
        public V value;

        public BSTNode<K, V> left;
        public BSTNode<K, V> right;

        /**
         * Constructor initializes this node's key, value, and children
         */
        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public BSTNode(BSTNode<K, V> o) {
            this.key = o.key;
            this.value = o.value;
            this.left = o.left;
            this.right = o.right;
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        public boolean hasBothChildren() {
            return this.left != null && this.right != null;
        }
    }

    /**
     * Initializes an empty Binary Search Tree
     */
    public BSTDictionary() {
        this.root = null;
        this.size = 0;
    }


    @Override
    public V get(K key) {
        BSTNode<K, V> targetNode = this.get(key, this.root);
        if (targetNode == null) {
            return null;
        }
        return targetNode.value;
   }

   private BSTNode<K,V> get(K key, BSTNode<K,V> current) {
        if (current == null) {
            return null;
        }
        if (key.compareTo(current.key) == 0) {
            return current;
        }
        if (key.compareTo(current.key) > 0) {
            return get(key, current.right);
        }
        return get(key, current.left);
   }


   private BSTNode<K,V> remove(K key, BSTNode<K,V> current) {
        if (current == null) {
            return null;
        }
        if (key.compareTo(current.key) == 0) {
            if (current.isLeaf()) {
                return null;
            }
            if (current.hasBothChildren()) {
                BSTNode<K,V> temp = current.right;
                while (temp.left != null) {
                    temp = temp.left;
                }

                current.right = this.remove(temp.key, current.right);

                temp.right = current.right;
                temp.left = current.left;
                current = new BSTNode<>(temp);
            }
            else if (current.right != null) {
                return current.right;
            }
            else {
                return current.left;
            }
        }
        else if (key.compareTo(current.key) > 0) {
            current.right = this.remove(key, current.right);
        }
        else {
            current.left = this.remove(key, current.left);
        }
        return current;
   }
    @Override
    public V remove(K key) {
        V value = this.get(key);
        this.root = this.remove(key, this.root);
        this.size--;

        if (value == null) {
            this.size++;
            return null;
        }

        return value;
    }

    private BSTNode<K,V> put(K key, V value, BSTNode<K,V> current) {
        if (current == null) {
            current = new BSTNode<>(key, value);
        }
        else if (key.compareTo(current.key) == 0) {
            current.value = value;
        }
        else if (key.compareTo(current.key) > 0) {
            current.right = this.put(key, value, current.right);
        }
        else {
            current.left = this.put(key, value, current.left);
        }
        return current;
    }

    @Override
    public V put(K key, V value) {
        V old = this.get(key);
        if(old == null) {
            this.size++;
        }

        this.root = this.put(key, value, this.root);
        return old;
    }

    @Override
    public boolean containsKey(K key) {
        return this.keys().contains(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key/value pairs in the BST
     */
    @Override
    public int size() {
        return this.size;
    }

    private void keys(IDeque<K> keys, BSTNode<K,V> current) {
        if (current != null) {
            keys.add(current.key);

            if(current.hasBothChildren()){
                keys(keys, current.left);
                keys(keys, current.right);
            }
            else if (current.left != null) {
                keys(keys, current.left);
            }
            else if (current.right != null) {
                keys(keys, current.right);
            }
        }
    }

    @Override
    public ICollection<K> keys() {
        IDeque<K> keys = new ArrayDeque<>();
        this.keys(keys, this.root);
        return keys;
    }

    private void values(IDeque<V> vals, BSTNode<K,V> current) {
        if (current != null) {
            vals.add(current.value);

            if(current.hasBothChildren()){
                values(vals, current.left);
                values(vals, current.right);
            }
            else if (current.left != null) {
                values(vals, current.left);
            }
            else if (current.right != null) {
                values(vals, current.right);
            }
        }
    }

    @Override
    public ICollection<V> values() {
        IDeque<V> vals = new ArrayDeque<>();
        this.values(vals, this.root);
        return vals;    }

    /**
     * Implementation of an iterator over the BST
     */

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }

    @Override
    public String toString() {
        if (this.root == null) {
            return "{}";
        }

        StringBuilder contents = new StringBuilder();

        IQueue<BSTNode<K, V>> nodes = new ArrayDeque<>();
        BSTNode<K, V> current = this.root;
        while (current != null) {
            contents.append(current.key + ": " + current.value + ", ");

            if (current.left != null) {
                nodes.enqueue(current.left);
            }
            if (current.right != null) {
                nodes.enqueue(current.right);
            }

            current = nodes.dequeue();
        }

        return "{" + contents.toString().substring(0, contents.length() - 2) + "}";
    }
}
