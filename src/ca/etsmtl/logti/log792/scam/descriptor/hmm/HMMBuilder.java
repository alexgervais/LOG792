package ca.etsmtl.logti.log792.scam.descriptor.hmm;

import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.label.Transcription;

public class HMMBuilder {

    private Corpus corpus;
    private Dictionary dictionary;
    private Transcription transcription;
    
    public HMMBuilder(Corpus corpus, Dictionary dictionary, Transcription transcription) {
        this.corpus = corpus;
        this.dictionary = dictionary;
        this.transcription = transcription;
    }
    
    public HMM buildPrototype() {
        return new HMM(corpus, dictionary, transcription);
    }

}
