package edu.caltech.cs2.datastructures;


import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Iterator;


public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;


    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }


    @Override
    public boolean isPrefix(K key) {
        if(this.root == null){
            return false;
        }
        TrieNode<A, V> current = this.root;
        for(A c : key) {
            if (current.pointers.get(c) == null) {
                return false;
            }
            current = current.pointers.get(c);
        }
        return true;
    }


    private void getcompletionsHelper(IDeque<V> dequeVals, K prefix, TrieNode<A,V> current){
        if(current == null){
            return;
        }
        if(current.value != null){
            dequeVals.add(current.value);
        }
        for(A c : current.pointers.keySet()){
            getcompletionsHelper(dequeVals, prefix, current.pointers.get(c));
        }
    }
    @Override
    public ICollection<V> getCompletions(K prefix) {
        IDeque<V> dequeVals = new ArrayDeque<>();

        if(!isPrefix(prefix)|| this.root == null){
            return dequeVals;
        }
        TrieNode<A,V> current = this.root;
        for(A pref : prefix){
            if(current != null){
                current = current.pointers.get(pref);
            }
        }
        getcompletionsHelper(dequeVals, prefix, current);
        return dequeVals;
    }


    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        if(this.root == null) {
            this.root = new TrieNode<>();
        }
        TrieNode<A, V> current = this.root;


        for(A c : key){
            if(!current.pointers.containsKey(c)){
                return null;
            }
            current = current.pointers.get(c);
        }
        return current.value;
    }


    @Override
    public V remove(K key) {
        return null;
    }


    @Override
    public V put(K key, V value) {
        if(this.root == null) {
            this.root = new TrieNode<>();
        }
        TrieNode<A, V> current = this.root;
        for(A c : key){
            if(!current.pointers.containsKey(c)){
                current.pointers.put(c, new TrieNode<>());
            }
            current = current.pointers.get(c);
        }
        V old_value = current.value;
        current.value = value;


        if(old_value == null){
            this.size++;
        }
        return old_value;
    }


    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }


    @Override
    public boolean containsValue(V value) {
        for (V val : values()){
            if (val.equals(value)){
                return true;
            }
        }
        return false;
    }


    @Override
    public int size() {
        return this.size;
    }


    private void keyHelper(TrieNode<A,V> pastNode, IDeque<A> lettersAccumulator, IDeque<K> keysAccumulator){
        if(pastNode == null){
            return;
        }


        if(pastNode.value != null){
            keysAccumulator.add(this.collector.apply(lettersAccumulator));
        }


        for(A c : pastNode.pointers.keySet()){
            lettersAccumulator.add(c);
            keyHelper(pastNode.pointers.get(c), lettersAccumulator, keysAccumulator);
            lettersAccumulator.removeBack();
        }
    }
    @Override
    public ICollection<K> keys() {
        IDeque<K> keysAccumulator = new ArrayDeque<>();
        IDeque<A> lettersAccumulator = new ArrayDeque<>();
        keyHelper(this.root, lettersAccumulator, keysAccumulator);
        return keysAccumulator;
    }
    @Override
    public ICollection<V> values() {
        ICollection<V> valuesAccumulator = new ArrayDeque<>();
        for(K thisKey: this.keys()){
            if(this.get(thisKey) != null){
                valuesAccumulator.add(this.get(thisKey));
            }
        }
        return valuesAccumulator;
    }


    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    private static class TrieNode<A, V> {
        public Map<A, TrieNode<A, V>> pointers;
        public V value;


        public TrieNode() {
            this(null);
        }


        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }


        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }


        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }


        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;


            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();


                if (node == null) {
                    continue;
                }


                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
