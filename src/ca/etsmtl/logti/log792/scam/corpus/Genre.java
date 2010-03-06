package ca.etsmtl.logti.log792.scam.corpus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Genre implements Comparable<Genre> {

    private final static Logger logger = Logger.getLogger(Genre.class);

    private List<Genre> subGenre = new ArrayList<Genre>();
    private String label = null;

    public Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void addSubGenre(Genre genre) {
        if (genre != null) {
            subGenre.add(0, genre);
        }
    }

    public List<Genre> getSubGenre() {
        return subGenre;
    }

    @Override
    public int compareTo(Genre o) {
        return this.getLabel().compareTo(o.getLabel());
    }

    @Override
    public String toString() {
        String output = "";
        Iterator<Genre> it = subGenre.iterator();
        while (it.hasNext()) {
            Genre sub = it.next();
            output += "." + sub.toString();
        }
        return this.label + output;
    }

}
