package ca.etsmtl.logti.log792.scam.descriptor.hmm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;

public class HMMBuilder {
    
    private final static Logger logger = Logger.getLogger(HMMBuilder.class);

    private Corpus corpus;
    private Dictionary dictionary;
    
    public HMMBuilder(Corpus corpus, Dictionary dictionary) {
        this.corpus = corpus;
        this.dictionary = dictionary;
        build();
    }
    
    public HMM buildPrototype() {
        return new HMM(corpus, dictionary);
    }
    
    protected void build() {
        File hmmPrototype = Config.getInstance().getHmmPrototype();
        if (!hmmPrototype.getParentFile().exists()) {
            hmmPrototype.getParentFile().mkdirs();
        }
        try {
            int vectorSize = Config.getInstance().getVectorSize();
            int stateCount = Config.getInstance().getHmmStateCount();
            int mixtureCount = Config.getInstance().getMixtures();
            double mixtureWeight = 1.0 / mixtureCount;
            String mfccType = Config.getInstance().getMfccType();
            
            StringBuffer buffer = new StringBuffer();
            buffer.append("  ~o <VecSize> " + vectorSize + " <" + mfccType + "> <StreamInfo> 1 " + vectorSize + "\n");
            buffer.append("  ~h \"" + hmmPrototype.getName() + "\"\n");
            buffer.append("<BeginHMM>\n");
            buffer.append("  <NumStates> " + stateCount + "\n");
            for (int i = 2; i < stateCount; i++) {
                buffer.append("  <State> " + i + " <NumMixes> " + mixtureCount + "\n");
                buffer.append("  <Stream> 1\n");
                for (int j = 1; j <= mixtureCount; j++) {
                    buffer.append("  <Mixture> " + j + " " + mixtureWeight + "\n");
                    buffer.append("    <Mean> " + vectorSize + "\n");
                    buffer.append("      ");
                    for (int k = 1; k <= vectorSize; k++) {
                        buffer.append("0.0 ");
                    }
                    buffer.append("\n");
                    buffer.append("    <Variance> " + vectorSize + "\n");
                    buffer.append("      ");
                    for (int k = 1; k <= vectorSize; k++) {
                        buffer.append("1.0 ");
                    }
                    buffer.append("\n");
                }
            }
            buffer.append("  <TransP> " + stateCount + "\n");
            double[][] transitions = new double[stateCount][stateCount];
            double startProbability = 1.0 / Config.getInstance().getSignaturesCount();
            double endProbability = 0.05;
            int stateStartIndex = 1;
            if (Config.getInstance().isSingle()) {
                transitionSignature(transitions, stateStartIndex, startProbability, endProbability, stateCount, 0);
                stateStartIndex += 1;
            } 
            if (Config.getInstance().isDuple()) {
                transitionSignature(transitions, stateStartIndex, startProbability, endProbability, stateCount, 1);
                stateStartIndex += 2;
            } 
            if (Config.getInstance().isTriple()) {
                transitionSignature(transitions, stateStartIndex, startProbability, endProbability, stateCount, 2);
                stateStartIndex += 3;
            } 
            if (Config.getInstance().isQuadruple()) {
                transitionSignature(transitions, stateStartIndex, startProbability, endProbability, stateCount, 3);
                stateStartIndex += 4;
            }
            for (int i = 0; i < transitions.length; i++) {
                buffer.append("    ");
                for (int j = 0; j < transitions[i].length; j++) {
                    buffer.append(transitions[i][j] + " ");
                }
                buffer.append("\n");
            }
            buffer.append("<EndHMM>\n");
            
            FileWriter writer = new FileWriter(hmmPrototype);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(buffer.toString());
            bufferedWriter.close();
            writer.close();
        } catch (IOException ex) {
            logger.error("Error while building HMM prototype", ex);
        }
    }
    
    private void transitionSignature(double[][] transitions, int startIndex, double startProbability, double endProbability, int stateCount, int size) {
        transitions[0][startIndex] = startProbability;
        transitions[startIndex + size][startIndex] = 0.05;
        int i;
        for (i = startIndex; i <= startIndex + size; i++) {
            transitions[i][i] = 0.95;
            if (i < startIndex + size) {
                transitions[i][i + 1] = 0.05;
            }
        }
        if (size > 0) {
            transitions[i - 1][i - 1] = 0.9;
        } else {
            transitions[i - 1][i - 1] = 0.95;
        }
        transitions[startIndex + size][stateCount - 1] = endProbability;
    }

}
