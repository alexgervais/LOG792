package ca.etsmtl.logti.log792.scam.descriptor.label;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Transcription {

    private Map<String, String> labels = new HashMap<String, String>();
    private File labelFile;
    private File alignedLabelFile = null;
    
    protected Transcription() {
        
    }
    
    public Map<String, String> getLabels() {
        return labels;
    }
    
    public File getFile() {
        return labelFile;
    }

    public File getAlignedFile() {
        return alignedLabelFile;
    }
    
    protected void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    protected void setFile(File labelFile) {
        this.labelFile = labelFile;
    }
    
    public void setAlignedFile(File alignedLabelFile) {
        this.alignedLabelFile = alignedLabelFile;
    }
    
}
