package ca.etsmtl.logti.log792.scam.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Genre;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class Evaluator {
    
    private final static Logger logger = Logger.getLogger(Evaluator.class);

    private List<Expert> experts = new ArrayList<Expert>();
    private Corpus corpus;
    private Dictionary dictionary;
    
    public Evaluator(Corpus corpus, Dictionary dictionary) {
        this.corpus = corpus;
        this.dictionary = dictionary;
    }
    
    public void evaluate() {
        Map<Track,Map<String, Integer>> expertResults = new HashMap<Track,Map<String, Integer>>();
        Iterator<Expert> it = experts.iterator();
        while (it.hasNext()) {
            try {
                Expert expert = it.next();
                Map<Track, String> results = expert.evaluate();
                Iterator<Track> trackIt = corpus.getTracks().iterator();
                while (trackIt.hasNext()) {
                    Track track = trackIt.next();
                    String genre = results.get(track);
                    int value = expert.getWeight();
                    if (expertResults.containsKey(track)) {
                        if (expertResults.get(track).containsKey(genre)) {
                            value += expertResults.get(track).remove(track);
                        }
                        expertResults.get(track).put(genre, value);
                    } else {
                        Map<String, Integer> genreValue = new HashMap<String, Integer>();
                        genreValue.put(genre, value);
                        expertResults.put(track, genreValue);
                    }
                }
            } catch (ScamException ex) {
                logger.error("Error during evaluation", ex);
            }
        }
        
        Iterator<Track> trackIt = corpus.getTracks().iterator();
        while (trackIt.hasNext()) {
            Track track = trackIt.next();
            Map<String, Integer> labelsCount = expertResults.get(track);
            if (labelsCount != null) {
                int maxCount = Integer.MIN_VALUE;
                String maxLabel = null;
                Iterator<String> dictionaryIt = dictionary.getEntries().iterator();
                while (dictionaryIt.hasNext()) {
                    String genreLabel = dictionaryIt.next();
                    if (labelsCount.containsKey(genreLabel) && labelsCount.get(genreLabel) > maxCount) {
                        maxCount = labelsCount.get(genreLabel);
                        maxLabel = genreLabel;
                    }
                }
                track.setEvaluatedGenre(new Genre(maxLabel));
            }
        }
    }
    
    public void addEpert(Expert expert) {
        experts.add(expert);
    }

}
