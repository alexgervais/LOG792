package ca.etsmtl.logti.log792.scam.test;

import java.util.Map;

import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public interface Expert {

    public Map<Track, String> evaluate() throws ScamException;
    
    public void setWeight(int weight);
    public int getWeight();    
}
