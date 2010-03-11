package ca.etsmtl.logti.log792.scam.train;

import java.util.List;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.DictionaryBuilder;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMM;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMMBuilder;
import ca.etsmtl.logti.log792.scam.descriptor.label.AudioLabler;
import ca.etsmtl.logti.log792.scam.descriptor.label.Transcription;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class Trainor {
    
    private final static Logger logger = Logger.getLogger(Trainor.class);
    
    private final int HMM_ITERATIONS = 10;

    private Corpus corpus = null;
    private HMM hmm = null;
    private Dictionary dictionary = null;
    private Transcription labels = null;
    
    public Trainor(Corpus corpus) {
        this.corpus = corpus;
    }
    
    public void train() throws ScamException {
        logger.info("Building dictionary");
        DictionaryBuilder dictionaryBuilder = new DictionaryBuilder(corpus);
        this.dictionary = dictionaryBuilder.build();
        
        logger.info("Building transcriptions");
        AudioLabler audioLabler = new AudioLabler(corpus);
        this.labels = audioLabler.build();
        
        HMMBuilder hmmBuilder = new HMMBuilder(corpus, dictionary, labels);
        logger.info("Building HMM prototype");
        HMM hmm = hmmBuilder.buildPrototype();
        logger.info("Refining HMM...");
        hmm.train();
        this.hmm = hmm;
    }

    public HMM getHmm() throws ScamException {
        if (hmm == null) {
            throw new ScamException("Training not completed");
        }
        return hmm;
    }

    public Dictionary getDictionary() throws ScamException {
        if (dictionary == null) {
            throw new ScamException("Training not completed");
        }
        return dictionary;
    }

    public Transcription getLabels() throws ScamException {
        if (labels == null) {
            throw new ScamException("Training not completed");
        }
        return labels;
    }
    
}
