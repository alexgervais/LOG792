package ca.etsmtl.logti.log792.scam.config;

import java.io.File;

public class Config {

    private static Config instance = null;
    
    private String trainingCorpusPath = null;
    private String testCorpusPath = null;
    private String workPath = null;
    
    private int hierarchyLevel;
    
    private String sox = null;
    private String htk = null;
    
    private File htkConfiguration = null;
    private File hmmPrototype = null;
    
    private File audioFolder = null;
    private File mfccRawFolder = null;
    private File mfccTxtFolder = null;
    private File hmmFolder = null;
    private File dictionaryFolder = null;
    private File transcriptionFolder = null;

    private int numceps;
    private boolean energy;
    private boolean delta;
    private boolean zero;
    private boolean acceleration;

    private boolean duple;
    private boolean triple;
    private boolean quadruple;
    
    private boolean hmmMultipleEvaluations;
    
    private int mixtures;
    
    private static final String MFCC = "MFCC";
    private static final String ENERGY = "_E";
    private static final String DELTA = "_D";
    private static final String ZERO = "_0";
    private static final String ACCELERATION = "_A";

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
    
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public String getSox() {
        return sox;
    }

    public String getHtk() {
        return htk;
    }

    public File getHtkConfiguration() {
        return htkConfiguration;
    }
    
    public File getHmmPrototype() {
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

    public int getNumceps() {
        return numceps;
    }

    public boolean isEnergy() {
        return energy;
    }

    public boolean isDelta() {
        return delta;
    }

    public boolean isZero() {
        return zero;
    }

    public boolean isAcceleration() {
        return acceleration;
    }

    public boolean isDuple() {
        return duple;
    }

    public boolean isTriple() {
        return triple;
    }

    public boolean isQuadruple() {
        return quadruple;
    }
    
    public boolean isHmmMultipleEvaluations() {
        return hmmMultipleEvaluations;
    }

    public int getMixtures() {
        return mixtures;
    }

    public int getVectorSize() {
        int totalSize = 0;
        int baseSize = getNumceps();
        
        if (isEnergy()) {
            baseSize++;
        }
        if (isZero()) {
            baseSize++;
        }
        totalSize = baseSize;
        if (isDelta()) {
            totalSize += baseSize;
        }
        if (isAcceleration()) {
            totalSize += baseSize;
        }
        return totalSize;
    }
    
    public String getMfccType() {
        String type = MFCC;
        if (isDelta()) {
            type += DELTA;
        }
        if (isEnergy()) {
            type += ENERGY;
        }
        if (isZero()) {
            type += ZERO;
        }
        if (isAcceleration()) {
            type += ACCELERATION;
        }
        return type;
    }
    
    public int getHmmStateCount() {
        int totalStates = 2;
        if (isDuple()) {
            totalStates += 2;
        }
        if (isTriple()) {
            totalStates += 3;
        }
        if (isQuadruple()) {
            totalStates += 4;
        }
        return totalStates;
    }
    
    public int getSignaturesCount() {
        int totalSignatures = 0;
        if (isDuple()) {
            totalSignatures++;
        }
        if (isTriple()) {
            totalSignatures++;
        }
        if (isQuadruple()) {
            totalSignatures++;
        }
        return totalSignatures;
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

    protected void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    protected void setSox(String sox) {
        this.sox = sox;
    }

    protected void setHtk(String htk) {
        this.htk = htk;
    }

    protected void setHtkConfiguration(File htkConfiguration) {
        this.htkConfiguration = htkConfiguration;
    }
    
    protected void setHmmPrototype(File hmmPrototype) {
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

    protected void setMfccNumceps(int numceps) {
        this.numceps = numceps;
    }

    protected void setMfccEnergy(boolean energy) {
        this.energy = energy;
    }

    protected void setMfccDelta(boolean delta) {
        this.delta = delta;
    }

    protected void setMfcc0(boolean zero) {
        this.zero = zero;
    }

    protected void setMfccAcceleration(boolean acceleration) {
        this.acceleration = acceleration;
        
        if (this.acceleration) {
            this.delta = true;
        }
    }

    protected void setHmmDuple(boolean duple) {
        this.duple = duple;
    }

    protected void setHmmTriple(boolean triple) {
        this.triple = triple;
    }

    protected void setHmmQuadruple(boolean quadruple) {
        this.quadruple = quadruple;
    }
    
    protected void setHmmMultipleEvaluations(boolean hmmMultipleEvaluations) {
        this.hmmMultipleEvaluations = hmmMultipleEvaluations;
    }

    protected void setMixtures(int mixtures) {
        this.mixtures = mixtures;
    }
    
}
