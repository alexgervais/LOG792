package ca.etsmtl.logti.log792.scam.descriptor.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.command.impl.CommandImpl;
import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Genre;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class DictionaryBuilder {
    
    private final static Logger logger = Logger.getLogger(DictionaryBuilder.class);

    private Corpus corpus;
    
    public DictionaryBuilder(Corpus corpus) {
        this.corpus = corpus;
    }
    
    public Dictionary build() throws ScamException {
        TreeSet<String> entries = new TreeSet<String>();
        Iterator<Track> itCorpus = corpus.getTracks().iterator();
        while (itCorpus.hasNext()) {
            Genre genre = itCorpus.next().getAcutalGenre();
            entries.add(genre.getLabel());
        }
        
        try {
            File dictionaryFile = new File(Config.getInstance().getDictionaryFolder(), corpus.getName() + ".dict");
            File grammarFile = new File(Config.getInstance().getDictionaryFolder(), corpus.getName() + ".grammar");
            File wordNetworkFile = new File(Config.getInstance().getDictionaryFolder(), corpus.getName() + ".wdnet");
            
            if (!dictionaryFile.exists()) {
                if (!Config.getInstance().getDictionaryFolder().exists()) {
                    Config.getInstance().getDictionaryFolder().mkdirs();
                }
                FileWriter writer = new FileWriter(dictionaryFile);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                Iterator<String> itEntries = entries.iterator();
                while (itEntries.hasNext()) {
                    String entry = itEntries.next();
                    bufferedWriter.write(entry.toUpperCase() + " " + entry + "\n");
                }
                bufferedWriter.close();
                writer.close();
            }
            
            if (!wordNetworkFile.exists()) {
                if (!Config.getInstance().getDictionaryFolder().exists()) {
                    Config.getInstance().getDictionaryFolder().mkdirs();
                }
                FileWriter writer = new FileWriter(grammarFile);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                if (Config.getInstance().isHmmMultipleEvaluations()) {
                    bufferedWriter.write("( <");
                } else {
                    bufferedWriter.write("(");
                }
                Iterator<String> itEntries = entries.iterator();
                while (itEntries.hasNext()) {
                    String entry = itEntries.next();
                    bufferedWriter.write(entry);
                    if (itEntries.hasNext()) {
                        bufferedWriter.write(" | ");
                    }
                }
                if (Config.getInstance().isHmmMultipleEvaluations()) {
                    bufferedWriter.write("> )");
                } else {
                    bufferedWriter.write(")");
                }
                bufferedWriter.close();
                writer.close();
                
                String[] command = { Config.getInstance().getHtk() + "HParse", grammarFile.getAbsolutePath(), wordNetworkFile.getAbsolutePath() };
                Command c = new CommandImpl(command);
                if (c.execute() != 0) {
                    throw new ScamException("Error building word network from grammar...");
                }
            }
            
            Dictionary dictionary = new Dictionary();
            List<String> list = new ArrayList<String>();
            list.addAll(entries);
            dictionary.setEntries(list);
            dictionary.setFile(dictionaryFile);
            dictionary.setWordNetworkFile(wordNetworkFile);
            
            return dictionary;
        } catch (IOException ex) {
            logger.error("Error while building dictionary", ex);
        }
        
        return null;
    }
}
