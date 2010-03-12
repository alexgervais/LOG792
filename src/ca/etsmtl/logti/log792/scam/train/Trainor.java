package ca.etsmtl.logti.log792.scam.train;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.DictionaryBuilder;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMM;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMMBuilder;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class Trainor {
    
    private final static Logger logger = Logger.getLogger(Trainor.class);
    
    private Corpus corpus = null;
    private HMM hmm = null;
    private Dictionary dictionary = null;
    
    public Trainor(Corpus corpus) {
        this.corpus = corpus;
    }
    
    public void train() throws ScamException {
        logger.info("Building dictionary");
        DictionaryBuilder dictionaryBuilder = new DictionaryBuilder(corpus);
        this.dictionary = dictionaryBuilder.build();
        
        logger.info("Building transcriptions");
        corpus.loadTranscriptions();
        
        HMMBuilder hmmBuilder = new HMMBuilder(corpus, dictionary);
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
    
}
