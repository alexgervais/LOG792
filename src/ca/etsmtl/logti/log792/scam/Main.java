package ca.etsmtl.logti.log792.scam;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Genre;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.exception.ScamException;
import ca.etsmtl.logti.log792.scam.test.Evaluator;
import ca.etsmtl.logti.log792.scam.train.Trainor;

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
                    if ((char) buf[i] != '\n' && (char) buf[i] != '\r') {
                        buffer.append((char) buf[i]);
                    } else if (buffer.length() > 0) {
                        logger.error(buffer.toString());
                        buffer.delete(0, buffer.length());
                    }
                }
            }

        };
        System.setErr(errProxy);

        Main program = new Main();
        program.run();
    }
    
    public Main() {
        
    }
    
    public void run() {
        logger.info("Loading training corpus from [" + Config.getInstance().getTrainingCorpusPath() + "]");
        Corpus trainCorpus = new Corpus(Config.getInstance().getTrainingCorpusPath(), "train");

        logger.info("Loading test corpus from [" + Config.getInstance().getTestCorpusPath() + "]");
        Corpus testCorpus = new Corpus(Config.getInstance().getTestCorpusPath(), "test");

        logger.info("Training...");
        try {
            Map<String, Trainor> trainors = train(trainCorpus);
        } catch (ScamException ex) {
            logger.error("Error during evaluation", ex);
        }
        logger.info("Training completed");

        logger.info("Evaluating...");
        /*
        try {
            Evaluator evaluator = new Evaluator(testCorpus, trainor.getHmm(), trainor.getDictionary());
            evaluator.evaluate();
        } catch (ScamException ex) {
            logger.error("Error during evaluation", ex);
        }
        */
        logger.info("Evaluation completed");
    }
    
    public Map<String, Trainor> train(Corpus corpus) throws ScamException {
        HashMap<String, Trainor> map = new HashMap<String, Trainor>();

        Trainor trainor = new Trainor(corpus);
        trainor.train();
        map.put(corpus.getName(), trainor);
        
        Iterator<String> entries = trainor.getDictionary().getEntries().iterator();
        while (entries.hasNext()) {
            Genre subGenre = new Genre(entries.next());
            Corpus subCorpus = corpus.getCorpusByGenre(subGenre);
            if (subCorpus.getTracks().size() > 0) {
                Map<String, Trainor> subTraining = train(corpus.getCorpusByGenre(subGenre));
                if (subTraining.size() > 0) {
                    map.putAll(subTraining);
                }
            }
        }
        
        return map;
    }
}