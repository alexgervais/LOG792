package ca.etsmtl.logti.log792.scam.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.corpus.Corpus;
import ca.etsmtl.logti.log792.scam.corpus.Track;
import ca.etsmtl.logti.log792.scam.descriptor.dictionary.Dictionary;

public class Reporter {

    private Corpus corpus;
    private Dictionary dictionary;
    
    public Reporter(Corpus corpus, Dictionary dictionary) {
        this.corpus = corpus;
        this.dictionary = dictionary;
    }
    
    public static void printConfig() {
        System.out.println("\n+++ CONFIGURATION +++\n");
        System.out.println("\tMFCC: " + Config.getInstance().getMfccType());
        System.out.println("\tSAMPLE-SIZE: " + Config.getInstance().getMfccSampleSize());
        System.out.println("\tWINDOW-SIZE: " + Config.getInstance().getMfccWindowSize());
        System.out.println("\tNUM-CEPS: " + Config.getInstance().getNumceps());
        System.out.println("\tNUM-MIXTURES: " + Config.getInstance().getMixtures());
        System.out.println("\tHIERARCHY-LEVEL: " + Config.getInstance().getHierarchyLevel());
        System.out.println("\tMULTIPLE-HMM-RESULTS: " + Config.getInstance().isHmmMultipleEvaluations());
        System.out.println("\tHMM-SINGLE: " + Config.getInstance().isSingle());
        System.out.println("\tHMM-DUPLE: " + Config.getInstance().isDuple());
        System.out.println("\tHMM-TRIPLE: " + Config.getInstance().isTriple());
        System.out.println("\tHMM-QUADRUPLE: " + Config.getInstance().isQuadruple());
        System.out.println("\n--- CONFIGURATION ---\n");
    }
    
    public void printResults() {
        System.out.println("\n+++ RESULTS [" + corpus.getName() + "] +++\n");
        Iterator<Track> it = corpus.getTracks().iterator();
        while (it.hasNext()) {
            Track track = it.next();
            System.out.println("\t" + track.getOriginalFile().getName() + " [" + track.getAcutalGenre().getLabel() + "] ==> [" + track.getEvaluatedGenre().getLabel() + "]");
        }
        System.out.println("\n--- RESULTS [" + corpus.getName() + "] ---\n");
    }
    
    public void printStatistics() {
        System.out.println("\n+++ STATISTICS [" + corpus.getName() + "] +++\n");
        int total = corpus.getTracks().size();
        int success = 0;
        int failures = 0;
        Iterator<Track> it = corpus.getTracks().iterator();
        while (it.hasNext()) {
            Track track = it.next();
            if (track.getAcutalGenre().getLabel().equals(track.getEvaluatedGenre().getLabel())) {
                success++;
            } else {
                failures++;
            }
        }
        System.out.println("\tTrack count : " + total);
        System.out.println("\tCorrectly classified: " + success);
        System.out.println("\tIncorrectly classified: " + failures);
        System.out.println("\tClassification error: " + (100.0 * failures) / total + " %");
        System.out.println("\tClassification rate: " + (100.0 * success) / total + " %");
        System.out.println("\n--- STATISTICS [" + corpus.getName() + "] ---\n");
    }
    
    public void printConfusionMatrix() {
        System.out.println("\n+++ CONFUSION MATRIX [" + corpus.getName() + "] +++\n");
        int total = dictionary.getEntries().size();
        
        Map<String,Map<String,Integer>> confusion = new HashMap<String, Map<String,Integer>>();
        Iterator<Track> trackIt = corpus.getTracks().iterator();
        while (trackIt.hasNext()) {
            Track track = trackIt.next();
            int value = 1;
            if (confusion.containsKey(track.getAcutalGenre().getLabel())) {
                if (confusion.get(track.getAcutalGenre().getLabel()).containsKey(track.getEvaluatedGenre().getLabel())) {
                    value += confusion.get(track.getAcutalGenre().getLabel()).remove(track.getEvaluatedGenre().getLabel());
                }
                confusion.get(track.getAcutalGenre().getLabel()).put(track.getEvaluatedGenre().getLabel(), value);
            } else {
                Map<String, Integer> confusionValue = new HashMap<String, Integer>();
                confusionValue.put(track.getEvaluatedGenre().getLabel(), value);
                confusion.put(track.getAcutalGenre().getLabel(), confusionValue);
            }
        }
        
        System.out.println("\t\t| Actual Values");
        
        
        Iterator<String> it = dictionary.getEntries().iterator();
        System.out.print("\t\t");
        while (it.hasNext()) {
            String genre = it.next();
            System.out.print(columFormat(genre));
        }
        System.out.println();
        System.out.println(rowSeparator(total));
        Iterator<String> itRows = dictionary.getEntries().iterator();
        while (itRows.hasNext()) {
            String actualGenre = itRows.next();
            System.out.print("\t" + rowFormat(actualGenre));
            Iterator<String> itColumns = dictionary.getEntries().iterator();
            while (itColumns.hasNext()) {
                String evaluatedGenre = itColumns.next();
                if (confusion.containsKey(evaluatedGenre)) {
                    if (confusion.get(evaluatedGenre).containsKey(actualGenre)) {
                        System.out.print(columFormat(String.valueOf(confusion.get(evaluatedGenre).get(actualGenre))));
                    } else {
                        System.out.print(columFormat("0"));
                    }
                } else {
                    System.out.print(columFormat("0"));
                }
            }
            System.out.println();
        }
        System.out.println(rowSeparator(total));
        System.out.println("\n--- CONFUSION MATRIX [" + corpus.getName() + "] ---\n");
    }
    
    private String columFormat(String label) {
        label = label + "      ";
        label = label.substring(0, 5);
        label = "| " + label + " ";
        return label;
    }
    
    private String rowFormat(String label) {
        label = label + "       ";
        label = label.substring(0, 6);
        label = " " + label + "\t";
        return label;
    }
    
    private String rowSeparator(int columns) {
        String line = "\t";
        for (int i = 0; i < (columns + 1) * 8; i++) {
            line += "-";
        }
        return line;
    }
    
}
