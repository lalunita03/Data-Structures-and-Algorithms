package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private static final int[] PRIMES = {5, 11, 23, 47, 97, 193, 389, 773,
            1549, 3089, 6173, 12347, 24697, 49393, 98779, 197551, 395107, 400009};
    private static final double FACTOR = 1.0;
    private int size;
    private IDeque<Integer> primes;
    private IDictionary<K,V>[] hashTable;

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        this.size = 0;
        this.primes = new ArrayDeque<>();
        for (int prime : PRIMES) {
            primes.addBack(prime);
        }

        this.hashTable = new IDictionary[primes.removeFront()];

        for (int i = 0; i < this.hashTable.length; i++) {
            this.hashTable[i] = this.chain.get();
        }

    }

    private int index(K key) {
        int i = key.hashCode() % this.hashTable.length;

        if (i < 0) {
            i += this.hashTable.length;
        }
        return i;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        return this.hashTable[this.index(key)].get(key);
    }

    @Override
    public V remove(K key) {
        V value = this.hashTable[this.index(key)].remove(key);
        if (value != null) {
            this.size--;
        }
        return value;
    }

    private void rehash() {
        if(this.size / this.hashTable.length > FACTOR) {
          //  IDictionary<K,V>[] newTable = new IDictionary[primes.removeFront()];
            IDictionary<K,V>[] oldTable = this.hashTable;
            this.hashTable = new IDictionary[primes.removeFront()];
            this.size = 0;
            for (int i = 0; i < this.hashTable.length; i++) {
                this.hashTable[i] = this.chain.get();
            }

            for (IDictionary<K,V> dict : oldTable) {
                for(K key : dict.keys()) {
                    this.put(key, dict.get(key));
                }
               // oldTable[i] = this.hashTable[i];
            }
           // this.hashTable = newTable;
        }
    }
    @Override
    public V put(K key, V value) {
        int i = this.index(key);

        V old = this.get(key);
        this.hashTable[i].put(key, value);

        if(old == null) {
            this.size++;
            this.rehash();
        }
        return old;
    }

    @Override
    public boolean containsKey(K key) {
        return this.keys().contains(key);
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ArrayDeque<K> keys = new ArrayDeque<>();
        for (IDictionary<K, V> dict : this.hashTable) {
            if (!dict.isEmpty()) {
                keys.addAll(dict.keys());
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ArrayDeque<V> vals = new ArrayDeque<>();
        for (IDictionary<K,V> dict : this.hashTable) {
            if(!dict.isEmpty()) {
                vals.addAll(dict.values());
            }
        }
        return vals;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }

    public String toString() {
        return this.values().toString();
    }
}
