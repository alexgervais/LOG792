package ca.etsmtl.logti.log792.scam.test.experts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.command.impl.CommandImpl;
import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;
import ca.etsmtl.logti.log792.scam.descriptor.hmm.HMM;
import ca.etsmtl.logti.log792.scam.descriptor.label.AudioLabler;
import ca.etsmtl.logti.log792.scam.exception.ScamException;
import ca.etsmtl.logti.log792.scam.test.Expert;

public class ExpertHMM implements Expert {

    private int weight = 0;
    private Corpus corpus;
    private HMM hmm;
    private Dictionary dictionary;
    
    public ExpertHMM(Corpus corpus, HMM hmm, Dictionary dictionary) {
        this.corpus = corpus;
        this.hmm = hmm;
        this.dictionary = dictionary;
    }
    
    @Override
    public Map<Track, String> evaluate() throws ScamException {
        File resultFile = new File(Config.getInstance().getHmmFolder(), corpus.getName() + ".results");
        if (!resultFile.exists()) {
            resultFile.getParentFile().mkdirs();
        }
        
        Iterator<Track> it = corpus.getTracks().iterator();
        List<String> commandArg = new ArrayList<String>();
        while (it.hasNext()) {
            commandArg.add("\"" + it.next().getRawMFCCFile().getAbsolutePath() + "\"");
        }
        
        String[] command = { Config.getInstance().getHtk() + "HVite", "-i", resultFile.getAbsolutePath(), "-o", "SWT", "-y", "\"\"", "-w", dictionary.getWordNetworkFile().getAbsolutePath(), "-H", hmm.getFile().getAbsolutePath(), dictionary.getFile().getAbsolutePath(), hmm.getListFile().getAbsolutePath() };
        List<String> args = new ArrayList<String>();
        args.addAll(commandArg);
        args.addAll(0, Arrays.asList(command));
    
        Command c = new CommandImpl(args.toArray(command));
        if (c.execute() != 0) {
            throw new ScamException("Error realining HMM...");
        }

        Map<String, String> fileResults = new HashMap<String, String>();
        try {
            FileReader fileReader = new FileReader(resultFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int readLineCount = 0;
            File testFile = null;
            Map<String, Integer> labelsCount = null;
            while((line = bufferedReader.readLine()) != null) {
                if (readLineCount == 0 && !line.equals(AudioLabler.MLF_HEADER)) {
                    throw new ScamException("MLF result file format invalid!");
                } else if (readLineCount > 0) {
                    if (line.contains("\"")) {
                        line = line.replace("\"", "");
                        testFile = new File(line);
                        labelsCount = new HashMap<String, Integer>();
                    } else if (line.equals(AudioLabler.MLF_SEPARATOR) && testFile != null && labelsCount != null) {
                        int maxCount = Integer.MIN_VALUE;
                        String maxLabel = null;
                        Iterator<String> dictionaryIt = dictionary.getEntries().iterator();
                        while (dictionaryIt.hasNext()) {
                            String genreLabel = dictionaryIt.next();
                            if (labelsCount.containsKey(genreLabel) && labelsCount.get(genreLabel) > maxCount) {
                                maxCount = labelsCount.get(genreLabel);
                                maxLabel = genreLabel;
                            }
                        }
                        fileResults.put(testFile.getName(), maxLabel);
                        
                        testFile = null;
                        labelsCount = null;
                    } else if (testFile != null && labelsCount != null) {
                        int count = 1;
                        if (labelsCount.containsValue(line)) {
                            count += labelsCount.remove(line);
                        }
                        labelsCount.put(line, count);
                    }
                }
                readLineCount++;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ScamException("Error reading results...", ex);
        }

        Map<Track, String> results = new HashMap<Track, String>();
        it = corpus.getTracks().iterator();
        while (it.hasNext()) {
            Track track = it.next();
            results.put(track, fileResults.get(track.getNormalizedAudioFile().getName()));
        }
        
        return results;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
    }

}
