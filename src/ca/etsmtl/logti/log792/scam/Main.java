package ca.etsmtl.logti.log792.scam;

import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("conf/log4j.properties");
        
        PrintStream errProxy = new PrintStream(System.err, true) {
            StringBuffer buffer = new StringBuffer();
            @Override
            public void write(byte[] buf, int off, int len) {
                super.write(buf, off, len);
                for (int i = off; i < len; i++) {
                    if ((char)buf[i] != '\n' && (char)buf[i] != '\r') {
                        buffer.append((char)buf[i]);
                    } else if (buffer.length() > 0) { 
                        logger.error(buffer.toString());
                        buffer.delete(0, buffer.length());
                    }
                }
            }
            
        };
        System.setErr(errProxy);
        
        logger.info("Loading training corpus from [" + Config.getInstance().getTrainingCorpusPath() + "]");
        Corpus trainCorpus = new Corpus(Config.getInstance().getTrainingCorpusPath(), "train");
        
        logger.info("Loading test corpus from [" + Config.getInstance().getTestCorpusPath() + "]");
        Corpus testCorpus = new Corpus(Config.getInstance().getTestCorpusPath(), "test");
    }
}