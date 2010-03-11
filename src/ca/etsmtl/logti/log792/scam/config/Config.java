package ca.etsmtl.logti.log792.scam.config;

import java.io.File;

public class Config {

    private static Config instance = null;
    
    private String trainingCorpusPath = null;
    private String testCorpusPath = null;
    private String workPath = null;
    
    private String sox = null;
    private String htk = null;
    private String htkConfiguration = null;
    private String hmmPrototype = null;
    
    private File audioFolder = null;
    private File mfccRawFolder = null;
    private File mfccTxtFolder = null;
    private File hmmFolder = null;
    private File dictionaryFolder = null;
    private File transcriptionFolder = null;

    private Config() {
        
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            new ConfigLoader().load();
        }
        return instance;
    }

    public String getTrainingCorpusPath() {
        return trainingCorpusPath;
    }

    public String getTestCorpusPath() {
        return testCorpusPath;
    }

    public String getWorkPath() {
        return workPath;
    }

    public String getSox() {
        return sox;
    }

    public String getHtk() {
        return htk;
    }

    public String getHtkConfiguration() {
        return htkConfiguration;
    }
    
    public String getHmmPrototype() {
        return hmmPrototype;
    }

    public File getAudioFolder() {
        return audioFolder;
    }

    public File getMfccRawFolder() {
        return mfccRawFolder;
    }

    public File getMfccTxtFolder() {
        return mfccTxtFolder;
    }

    public File getHmmFolder() {
        return hmmFolder;
    }

    public File getDictionaryFolder() {
        return dictionaryFolder;
    }

    public File getTranscriptionFolder() {
        return transcriptionFolder;
    }

    protected static void setInstance(Config instance) {
        Config.instance = instance;
    }

    protected void setTrainingCorpusPath(String trainingCorpusPath) {
        this.trainingCorpusPath = trainingCorpusPath;
    }

    protected void setTestCorpusPath(String testCorpusPath) {
        this.testCorpusPath = testCorpusPath;
    }

    protected void setWorkPath(String workPath) {
        this.workPath = workPath;
    }

    protected void setSox(String sox) {
        this.sox = sox;
    }

    protected void setHtk(String htk) {
        this.htk = htk;
    }

    protected void setHtkConfiguration(String htkConfiguration) {
        this.htkConfiguration = htkConfiguration;
    }
    
    protected void setHmmPrototype(String hmmPrototype) {
        this.hmmPrototype = hmmPrototype;
    }

    protected void setAudioFolder(File audioFolder) {
        this.audioFolder = audioFolder;
    }

    protected void setMfccRawFolder(File mfccRawFolder) {
        this.mfccRawFolder = mfccRawFolder;
    }

    protected void setMfccTxtFolder(File mfccTxtFolder) {
        this.mfccTxtFolder = mfccTxtFolder;
    }

    protected void setHmmFolder(File hmmFolder) {
        this.hmmFolder = hmmFolder;
    }

    protected void setDictionaryFolder(File dictionaryFolder) {
        this.dictionaryFolder = dictionaryFolder;
    }

    protected void setTranscriptionFolder(File transcriptionFolder) {
        this.transcriptionFolder = transcriptionFolder;
    }

    
}
