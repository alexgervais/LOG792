package ca.etsmtl.logti.log792.scam.corpus;

import java.io.File;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.audio.AudioTransform;
import ca.etsmtl.logti.log792.scam.audio.FeatureExtractor;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class Track implements Comparable<Track> {

    private final static Logger logger = Logger.getLogger(Track.class);

    private File originalFile = null;
    private File audioFile = null;
    private File rawMFCC = null;
    private File textMFCC = null;

    private Genre actualGenre = null;
    private Genre evaluatedGenre = null;

    public Track(File file, Genre genre) throws ScamException {
        originalFile = file;
        actualGenre = genre;

        extract();
        logger.info("Track [" + originalFile.getName() + "] genre [" + actualGenre.toString() + "]");
    }

    private void extract() throws ScamException {
        logger.debug("extract for file [" + originalFile.getAbsolutePath() + "]");

        AudioTransform audioTransform = new AudioTransform(originalFile);
        audioFile = audioTransform.transform();
        
        FeatureExtractor featureExtractor = new FeatureExtractor(audioFile);
        rawMFCC = featureExtractor.extractRawMFCC();
        textMFCC = featureExtractor.extractTxtMFCC();
    }

    public File getOriginalFile() {
        return originalFile;
    }
    
    public File getNormalizedAudioFile() {
        return audioFile;
    }

    public File getRawMFCCFile() {
        return rawMFCC;
    }

    public File getMFCCFile() {
        return textMFCC;
    }

    public Genre getAcutalGenre() {
        return actualGenre;
    }

    public Genre getEvaluatedGenre() {
        return evaluatedGenre;
    }

    public void setEvaluatedGenre(Genre genre) {
        evaluatedGenre = genre;
    }

    @Override
    public int compareTo(Track o) {
        return this.getOriginalFile().getName().compareTo(o.getOriginalFile().getName());
    }

}
