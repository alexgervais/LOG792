package ca.etsmtl.logti.log792.scam.descriptor.dictionary;

import java.io.File;
import java.util.List;

public class Dictionary {

    private List<String> entries;
    private File dictionaryFile;
    private File wordNetworkFile;
    
    protected Dictionary() {
        
    }
    
    public File getFile() {
        return dictionaryFile;
    }
    
    public File getWordNetworkFile() {
        return wordNetworkFile;
    }
    
    public List<String> getEntries() {
        return entries;
    }

    protected void setEntries(List<String> entries) {
        this.entries = entries;
    }

    protected void setFile(File file) {
        this.dictionaryFile = file;
    }
    
    protected void setWordNetworkFile(File file) {
        this.wordNetworkFile = file;
    }
    
}
