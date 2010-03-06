package ca.etsmtl.logti.log792.scam.test;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.Main;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMM;

public class Evaluator {
    
    private final static Logger logger = Logger.getLogger(Main.class);
    
    private Corpus corpus;
    private HMM hmm;
    private Dictionary dictionary;
    
    public Evaluator(Corpus corpus, HMM hmm, Dictionary dictionary) {
        this.corpus = corpus;
        this.hmm = hmm;
        this.dictionary = dictionary;
    }
    
    public void evaluate() {
        // TODO
    }

}
