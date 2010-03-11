package ca.etsmtl.logti.log792.scam.descriptor.hmm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.command.impl.CommandImpl;
import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.label.Transcription;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class HMM {

    private final static Logger logger = Logger.getLogger(HMM.class);
    
    private File hmmFile;
    private Map<String, File> hmmFiles;
    private Corpus corpus;
    private Dictionary dictionary;
    private Transcription transcription;
    
    protected HMM(Corpus corpus, Dictionary dictionary, Transcription transcription) {
        this.corpus = corpus;
        this.dictionary = dictionary;
        this.transcription = transcription;
        hmmFiles = new HashMap<String, File>();
        
        Iterator<String> itDictionaryEntries = dictionary.getEntries().iterator();
        while (itDictionaryEntries.hasNext()) {
            hmmFiles.put(itDictionaryEntries.next(), new File(Config.getInstance().getHmmPrototype()));
        }
    }
    
    public File getFile() {
        return hmmFile;
    }
    
    
    public void train() throws ScamException {
        Iterator<Track> it = corpus.getTracks().iterator();
        List<String> commandArg = new ArrayList<String>();
        while (it.hasNext()) {
            commandArg.add("\"" + it.next().getRawMFCCFile().getAbsolutePath() + "\"");
        }
        
        File rootHmmFolder = Config.getInstance().getHmmFolder();
        if (corpus.getParent() != null) {
            rootHmmFolder = new File(rootHmmFolder, corpus.getParent());
        }
        if (!rootHmmFolder.exists()) {
            rootHmmFolder.mkdirs();
        }
        
        File hmmList = new File(rootHmmFolder, "list");
        if (!hmmList.exists()) {
            Iterator<String> itDictionaryEntries = dictionary.getEntries().iterator();
            while (itDictionaryEntries.hasNext()) {
                try  {
                    FileWriter writer = new FileWriter(hmmList, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.write(itDictionaryEntries.next() + "\n");
                    bufferedWriter.close();
                    writer.close();
                } catch (Throwable ex) {
                    new ScamException("Error while writing hmm list", ex);
                }
            }
        }
        
        for (int i = 0; i <= 3; i++) {
            File hmmFolder = new File(rootHmmFolder, String.valueOf(i));
            if (!hmmFolder.exists()) {
                hmmFolder.mkdirs();
            }
            
            Iterator<String> itDictionaryEntries = dictionary.getEntries().iterator();
            while (itDictionaryEntries.hasNext()) {
                String genre = itDictionaryEntries.next();
                File oldHmmFile = hmmFiles.remove(genre);
                File newHmmFile = new File(hmmFolder, genre);
                if (!newHmmFile.exists()) {         
                    switch (i) {
                        case 0:
                            init(commandArg, genre, newHmmFile, oldHmmFile);
                            break;
                        case 1:
                            initAligned(commandArg, genre, newHmmFile, oldHmmFile);
                            break;
                        case 2:
                            refine(commandArg, genre, newHmmFile, oldHmmFile);
                            break;
                        case 3:
                            hmmFile = new File(newHmmFile.getParent(), corpus.getName());
                            if (!hmmFile.exists()) {
                                refineAll(commandArg, hmmList, hmmFile, oldHmmFile);
                            }
                            break;
                    }
                }
                hmmFiles.put(genre, newHmmFile);
            }
            if (!transcription.getAlignedFile().exists()) {
                align(commandArg, hmmList, hmmFolder);
            }
        }
    }
    
    private void init(List<String> fileList, String genre, File newHmmFile, File oldHmmFile) throws ScamException {
        List<String> args = new ArrayList<String>();
        String[] command = { Config.getInstance().getHtk() + "HCompV", "-A", "-T", "10", "-m", "-l", genre, "-o", newHmmFile.getName(), "-M", newHmmFile.getParent(), "-I", transcription.getFile().getAbsolutePath(), oldHmmFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error initializing HMM...");
        }
    }
    
    private void align(List<String> fileList, File hmmListFile, File hmmFolder) throws ScamException {
        List<String> args = new ArrayList<String>();
        String[] command = { Config.getInstance().getHtk() + "HVite", "-A", "-T", "100", "-a", "-i", transcription.getAlignedFile().getAbsolutePath(), "-m", "-I", transcription.getFile().getAbsolutePath(), "-y", "lab", "-d", hmmFolder.getAbsolutePath(), dictionary.getFile().getAbsolutePath(), hmmListFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error realining HMM...");
        }
    }
    
    private void initAligned(List<String> fileList, String genre, File newHmmFile, File oldHmmFile) throws ScamException {
        List<String> args = new ArrayList<String>();
        String[] command = { Config.getInstance().getHtk() + "HInit", "-A", "-T", "10", "-m", "1", "-l", genre, "-o", newHmmFile.getName(), "-M", newHmmFile.getParent(), "-I", transcription.getAlignedFile().getAbsolutePath(), oldHmmFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error initializing aligned HMM...");
        }
    }
    
    private void refine(List<String> fileList, String genre, File newHmmFile, File oldHmmFile) throws ScamException {
        List<String> args = new ArrayList<String>();        
        String[] command = { Config.getInstance().getHtk() + "HRest", "-A", "-T", "10", "-m", "1", "-l", genre, "-M", newHmmFile.getParent(), "-I", transcription.getAlignedFile().getAbsolutePath(), oldHmmFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error refining HMM...");
        }
    }
    
    private void refineAll(List<String> fileList, File hmmListFile, File newHmmFile, File oldHmmFolder) throws ScamException {
        List<String> args = new ArrayList<String>();        
        String[] command = { Config.getInstance().getHtk() + "HERest", "-A", "-T", "10", "-M", newHmmFile.getParent(), "-I", transcription.getAlignedFile().getAbsolutePath(), "-d", oldHmmFolder.getParent(), "-o", "wtf", hmmListFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error refining all HMM...");
        }
        
        File newMacros = new File(newHmmFile.getParent(), "newMacros");
        newMacros.renameTo(newHmmFile);
    }
}
