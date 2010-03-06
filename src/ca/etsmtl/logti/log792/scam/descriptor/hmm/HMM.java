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
            hmmFiles.put(itDictionaryEntries.next(), new File("c:/scam/", "C.txt"));
        }
    }
    
    public Map<String, File> getFiles() {
        return hmmFiles;
    }
    
    
    public void train(int iteration) throws ScamException {
        Iterator<Track> it = corpus.getTracks().iterator();
        List<String> commandArg = new ArrayList<String>();
        while (it.hasNext()) {
            commandArg.add("\"" + it.next().getRawMFCCFile().getAbsolutePath() + "\"");
        }
        
        File hmmFolder = Config.getInstance().getHmmFolder();
        if (corpus.getParent() != null) {
            hmmFolder = new File(hmmFolder, corpus.getParent());
        }
        hmmFolder = new File(hmmFolder, String.valueOf(iteration));
        if (!hmmFolder.exists()) {
            hmmFolder.mkdirs();
        }
        File hmmList = new File(hmmFolder, "list");
        
        Iterator<String> itDictionaryEntries = dictionary.getEntries().iterator();
        while (itDictionaryEntries.hasNext()) {
            String genre = itDictionaryEntries.next();
            File oldHmmFile = hmmFiles.remove(genre);
            File newHmmFile = new File(hmmFolder, genre);
            if (!newHmmFile.exists()) {         
                if (iteration == 0) {
                    try  {
                        FileWriter writer = new FileWriter(hmmList, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(writer);
                        bufferedWriter.write(genre + "\n");
                        bufferedWriter.close();
                        writer.close();
                    } catch (Throwable ex) {
                        new ScamException("Error while writing hmm list", ex);
                    }
                    init(commandArg, genre, newHmmFile, oldHmmFile);
                } else if (iteration > 0) {
                    if (iteration == 1) {
                        try {
                            initAligned(commandArg, genre, newHmmFile, oldHmmFile);
                        } catch (ScamException ex) {
                            logger.warn("Init failed, moving previous HMM iteration as current");
                            oldHmmFile.renameTo(newHmmFile);
                        }
                    } else {
                        refine(commandArg, genre, newHmmFile, oldHmmFile);
                    }
                }
            }
            hmmFiles.put(genre, newHmmFile);
        }
        
        if (iteration == 0) {
            File alignedLabelFile = new File(transcription.getFile().getParent(), "aligned." + transcription.getFile().getName());
            transcription.setAlignedFile(alignedLabelFile);
            align(commandArg, hmmList, hmmFolder);
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
        String[] command = { Config.getInstance().getHtk() + "HVite", "-A", "-T", "100", "-a", "-i", transcription.getAlignedFile().getAbsolutePath(), "-l", transcription.getFile().getParent(), "-m", "-I", transcription.getFile().getAbsolutePath(), "-y", "none", "-d", hmmFolder.getAbsolutePath(), dictionary.getFile().getAbsolutePath(), hmmListFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error realining HMM...");
        }
    }
    
    private void initAligned(List<String> fileList, String genre, File newHmmFile, File oldHmmFile) throws ScamException {
        List<String> args = new ArrayList<String>();
        String[] command = { Config.getInstance().getHtk() + "HInit", "-A", "-T", "100", "-l", genre, "-o", newHmmFile.getName(), "-M", newHmmFile.getParent(), "-I", transcription.getAlignedFile().getAbsolutePath(), oldHmmFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error initializing aligned HMM...");
        }
    }
    
    private void refine(List<String> fileList, String genre, File newHmmFile, File oldHmmFile) throws ScamException {
        List<String> args = new ArrayList<String>();        
        String[] command = { Config.getInstance().getHtk() + "HRest", "-A", "-T", "100", "-l", genre, "-M", newHmmFile.getParent(), "-I", transcription.getFile().getAbsolutePath(), oldHmmFile.getAbsolutePath() };
        args.addAll(fileList);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error refining HMM...");
        }
    }
}
