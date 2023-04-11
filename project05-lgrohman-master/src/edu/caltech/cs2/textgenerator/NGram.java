package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;
public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;

    public static String normalize(String s) {
        return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (String word : x) {
            this.data.add(word);
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        Iterator<String> dataIterator = this.data.iterator();
        dataIterator.next();
        for (int i = 0; i < data.length - 1; i++) {
            data[i] = dataIterator.next();
        }
        data[data.length - 1] = word;
        return new NGram(data);
    }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ? "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }

    @Override
    public int compareTo(NGram other) {
        if(this.equals(other)) {
            return 0;
        }
        if (this.data.size() > other.data.size()) {
            return 1;
        }
        if (this.data.size() < other.data.size()) {
            return -1;
        }

        Iterator<String> iterate = this.iterator();
        Iterator<String> oIterate = other.iterator();

        String current = "";
        String oCurrent = "";
        int sum = 0;

        for (int i = 0; i < this.data.size(); i++) {
            current = iterate.next();
            oCurrent = oIterate.next();
            sum += current.compareTo(oCurrent);
            if (sum != 0) {
                return sum;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NGram)) {
            return false;
        }
        NGram oGram = (NGram) o;

        if(oGram.data.size() != this.data.size()) {
            return false;
        }

        Iterator<String> iterate = this.iterator();
        Iterator<String> oIterate = oGram.iterator();

        String current = "";
        String oCurrent = "";

        for (int i = 0; i < this.data.size(); i++) {
            current = iterate.next();
            oCurrent = oIterate.next();

            if(!current.equals(oCurrent)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Iterator<String> iterate = this.iterator();
        int code = 0;

        for(int i = 0; i < this.data.size(); i++) {
            code = code * 31 + iterate.next().hashCode();
        }
        return code;
    }
}