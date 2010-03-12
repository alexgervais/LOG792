package ca.etsmtl.logti.log792.scam;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Genre;
import ca.etsmtl.logti.log792.scam.exception.ScamException;
import ca.etsmtl.logti.log792.scam.test.Evaluator;
import ca.etsmtl.logti.log792.scam.test.Expert;
import ca.etsmtl.logti.log792.scam.test.Reporter;
import ca.etsmtl.logti.log792.scam.test.experts.ExpertHMM;
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
        Reporter.printConfig();
        
        logger.info("Loading training corpus from [" + Config.getInstance().getTrainingCorpusPath() + "]");
        Corpus trainCorpus = new Corpus(Config.getInstance().getTrainingCorpusPath(), "train");

        logger.info("Training...");
        Map<String, Trainor> trainors = null;
        try {
            trainors = train(trainCorpus, 1);
        } catch (ScamException ex) {
            logger.error("Error during evaluation", ex);
        }
        logger.info("Training completed");
        
        logger.info("Loading test corpus from [" + Config.getInstance().getTestCorpusPath() + "]");
        Corpus testCorpus = new Corpus(Config.getInstance().getTestCorpusPath(), "test");

        if (trainors != null) {
            logger.info("Evaluating...");
            evaluate(testCorpus, trainCorpus, trainors, 1);            
            logger.info("Evaluation completed");
        }
        logger.info("SCAM will exit");
    }
    
    public Map<String, Trainor> train(Corpus corpus, int level) throws ScamException {
        if (level > Config.getInstance().getHierarchyLevel()) {
            return null;
        }
        
        HashMap<String, Trainor> map = new HashMap<String, Trainor>();

        Trainor trainor = new Trainor(corpus);
        trainor.train();
        map.put(corpus.getName(), trainor);
        
        Iterator<String> entries = trainor.getDictionary().getEntries().iterator();
        while (entries.hasNext()) {
            Genre subGenre = new Genre(entries.next());
            Corpus subCorpus = corpus.getCorpusByGenre(subGenre);
            if (subCorpus.getTracks().size() > 0) {
                Map<String, Trainor> subTraining = train(corpus.getCorpusByGenre(subGenre), level + 1);
                if (subTraining != null && subTraining.size() > 0) {
                    map.putAll(subTraining);
                }
            }
        }
        
        return map;
    }
    
    public void evaluate(Corpus testCorpus, Corpus trainCorpus, Map<String, Trainor> trainors, int level) {
        if (level > Config.getInstance().getHierarchyLevel()) {
            return;
        }
        
        Trainor trainor = trainors.get(trainCorpus.getName());
        
        try {
            Expert hmmExpert = new ExpertHMM(testCorpus, trainor.getHmm(), trainor.getDictionary());
            Evaluator evaluator = new Evaluator(testCorpus, trainor.getDictionary());
            evaluator.addEpert(hmmExpert);
            evaluator.evaluate();
            logger.info("Printing results for corpus [" + testCorpus.getName() + "]");
            Reporter report = new Reporter(testCorpus, trainor.getDictionary());
            report.printResults();
            report.printStatistics();
            report.printConfusionMatrix();
        } catch (ScamException ex) {
            logger.error("Error during evaluation", ex);
        }
        
        try {
            Iterator<String> entries = trainor.getDictionary().getEntries().iterator();
            while (entries.hasNext()) {
                Genre subGenre = new Genre(entries.next());
                Corpus subCorpus = trainCorpus.getCorpusByGenre(subGenre);
                if (subCorpus.getTracks().size() > 0) {
                    evaluate(subCorpus, trainCorpus.getCorpusByGenre(subGenre), trainors, level + 1);
                }
            }
        } catch (ScamException ex) {
            logger.error("Error during evaluation of sub-genres", ex);
        }
    }
}