package ca.etsmtl.logti.log792.scam.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {
    
    private final static Logger logger = Logger.getLogger(ConfigLoader.class);
    
    private final static String AUDIO_FOLDER = "audio";
    private final static String MFCC_RAW_FOLDER = "mfcc/raw";
    private final static String MFCC_TXT_FOLDER = "mfcc/text";
    private final static String HMM_FOLDER = "hmm";
    private final static String DICTIONARY_FOLDER = "dictionary";
    private final static String TRANSCRIPTION_FOLDER = "transcriptions";
    
    File propertiesFile = new File("conf/scam.properties");
    
    protected ConfigLoader() {
        
    }
    
    protected void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            
            Config.getInstance().setTrainingCorpusPath(properties.getProperty("corpus.training.path"));
            Config.getInstance().setTestCorpusPath(properties.getProperty("corpus.test.path"));
            
            Config.getInstance().setWorkPath(properties.getProperty("work.path"));
            
            Config.getInstance().setSox(properties.getProperty("sox.path"));
            
            Config.getInstance().setHtk(properties.getProperty("htk.path"));
            Config.getInstance().setHtkConfiguration(properties.getProperty("htk.conf"));
            Config.getInstance().setHmmPrototype(properties.getProperty("hmm.prototype"));
            
            Config.getInstance().setAudioFolder(new File(Config.getInstance().getWorkPath(), AUDIO_FOLDER));
            Config.getInstance().setMfccRawFolder(new File(Config.getInstance().getWorkPath(), MFCC_RAW_FOLDER));
            Config.getInstance().setMfccTxtFolder(new File(Config.getInstance().getWorkPath(), MFCC_TXT_FOLDER));
            Config.getInstance().setHmmFolder(new File(Config.getInstance().getWorkPath(), HMM_FOLDER));
            Config.getInstance().setDictionaryFolder(new File(Config.getInstance().getWorkPath(), DICTIONARY_FOLDER));
            Config.getInstance().setTranscriptionFolder(new File(Config.getInstance().getWorkPath(), TRANSCRIPTION_FOLDER));
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
