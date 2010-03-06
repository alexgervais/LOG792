package ca.etsmtl.logti.log792.scam.descriptor.label;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Track;

public class AudioLabler {
    
    private final static Logger logger = Logger.getLogger(AudioLabler.class);
    
    private final String MLF_HEADER = "#!MLF!#";
    private final String LAB_EXTENSION = ".lab";

    private Corpus corpus;
    
    public AudioLabler(Corpus corpus) {
        this.corpus = corpus;
    }
    
    public Transcription build() {
        
        
        try {
            File mlfFile = new File(Config.getInstance().getTranscriptionFolder(), corpus.getName() + ".mlf");
            Map<String, String> entries = new HashMap<String, String>();
            if (!mlfFile.exists()) {
                if (!Config.getInstance().getTranscriptionFolder().exists()) {
                    Config.getInstance().getTranscriptionFolder().mkdirs();
                }
                FileWriter writer = new FileWriter(mlfFile);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(MLF_HEADER + "\n");
                
                Iterator<Track> itCorpus = corpus.getTracks().iterator();
                while (itCorpus.hasNext()) {
                    Track track = itCorpus.next();
                    entries.put(track.getOriginalFile().getName(), track.getAcutalGenre().getLabel());
                    bufferedWriter.write("\"*/" + track.getNormalizedAudioFile().getName() + LAB_EXTENSION + "\"\n");
                    bufferedWriter.write(track.getAcutalGenre().getLabel() + "\n");
                    bufferedWriter.write(".\n");
                }
                bufferedWriter.close();
                writer.close();
            }
            
            Transcription mlf = new Transcription();
            mlf.setLabels(entries);
            mlf.setFile(mlfFile);
            return mlf;
        } catch (IOException ex) {
            logger.error("Error while building dictionary", ex);
        }
        
        return null;
        
    }
}
