package ca.etsmtl.logti.log792.scam.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {
    
    private final static Logger logger = Logger.getLogger(ConfigLoader.class);
    
    private final static String AUDIO_FOLDER = "audio";
    private final static String MFCC_CONFIG = "mfcc/HTK.conf";
    private final static String MFCC_RAW_FOLDER = "mfcc/raw";
    private final static String MFCC_TXT_FOLDER = "mfcc/text";
    private final static String HMM_FOLDER = "hmm";
    private final static String HMM_PROTOTYPE = "hmm/prototype";
    private final static String DICTIONARY_FOLDER = "dictionary";
    private final static String TRANSCRIPTION_FOLDER = "transcriptions";
    
    File propertiesFile = new File("conf/scam.properties");
    
    protected ConfigLoader() {
        
    }
    
    protected void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            
            Config.getInstance().setTrainingCorpusPath(properties.getProperty("corpus.training.path").trim());
            Config.getInstance().setTestCorpusPath(properties.getProperty("corpus.test.path").trim());
            Config.getInstance().setHierarchyLevel(Integer.parseInt(properties.getProperty("corpus.level").trim()));
            
            Config.getInstance().setWorkPath(properties.getProperty("work.path").trim());
            
            Config.getInstance().setSox(properties.getProperty("sox.path").trim());
            Config.getInstance().setHtk(properties.getProperty("htk.path").trim());
            
            Config.getInstance().setMfccSampleSize(Integer.parseInt(properties.getProperty("mfcc.samplesize").trim()));
            Config.getInstance().setMfccWindowSize(Integer.parseInt(properties.getProperty("mfcc.windowsize").trim()));
            Config.getInstance().setMfccNumceps(Integer.parseInt(properties.getProperty("mfcc.numceps").trim()));
            Config.getInstance().setMfccEnergy(Boolean.parseBoolean(properties.getProperty("mfcc.energy").trim()));
            Config.getInstance().setMfccDelta(Boolean.parseBoolean(properties.getProperty("mfcc.delta").trim()));
            Config.getInstance().setMfcc0(Boolean.parseBoolean(properties.getProperty("mfcc.0").trim()));
            Config.getInstance().setMfccAcceleration(Boolean.parseBoolean(properties.getProperty("mfcc.acceleration").trim()));
            
            Config.getInstance().setHmmSingle(Boolean.parseBoolean(properties.getProperty("hmm.single").trim()));
            Config.getInstance().setHmmDuple(Boolean.parseBoolean(properties.getProperty("hmm.duple").trim()));
            Config.getInstance().setHmmTriple(Boolean.parseBoolean(properties.getProperty("hmm.triple").trim()));
            Config.getInstance().setHmmQuadruple(Boolean.parseBoolean(properties.getProperty("hmm.quadruple").trim()));
            
            Config.getInstance().setHmmMultipleEvaluations(Boolean.parseBoolean(properties.getProperty("hmm.multipleevaluations").trim()));
            
            Config.getInstance().setMixtures(Integer.parseInt(properties.getProperty("hmm.mixtures").trim()));
            
            Config.getInstance().setHtkConfiguration(new File(Config.getInstance().getWorkPath(), MFCC_CONFIG));
            Config.getInstance().setHmmPrototype(new File(Config.getInstance().getWorkPath(), HMM_PROTOTYPE));
            
            Config.getInstance().setAudioFolder(new File(Config.getInstance().getWorkPath(), AUDIO_FOLDER));
            Config.getInstance().setMfccRawFolder(new File(Config.getInstance().getWorkPath(), MFCC_RAW_FOLDER));
            Config.getInstance().setMfccTxtFolder(new File(Config.getInstance().getWorkPath(), MFCC_TXT_FOLDER));
            Config.getInstance().setHmmFolder(new File(Config.getInstance().getWorkPath(), HMM_FOLDER));
            Config.getInstance().setDictionaryFolder(new File(Config.getInstance().getWorkPath(), DICTIONARY_FOLDER));
            Config.getInstance().setTranscriptionFolder(new File(Config.getInstance().getWorkPath(), TRANSCRIPTION_FOLDER));
            
            HtkConfigBuilder.build();
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
