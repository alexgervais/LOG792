package ca.etsmtl.logti.log792.scam.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class HtkConfigBuilder {

    private final static Logger logger = Logger.getLogger(HtkConfigBuilder.class);

    protected static void build() {
        File htkConfig = Config.getInstance().getHtkConfiguration();
        if (!htkConfig.getParentFile().exists()) {
            htkConfig.getParentFile().mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(htkConfig);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write("# Feature configuration\n" + 
                    "TARGETKIND = " + Config.getInstance().getMfccType() + "\n" + 
                    "TARGETRATE = 100000\n" + 
                    "TARGETFORMAT = HTK\n" + 
                    "SAVECOMPRESSED = T\n" + 
                    "SAVEWITHCRC = F\n" + 
                    "WINDOWSIZE = 100000\n" + 
                    "USEHAMMING = T\n" + 
                    "PREEMCOEF = 0.97\n" + 
                    "NUMCHANS = 26\n" + 
                    "CEPLIFTER = 22\n" + 
                    "NUMCEPS = " + Config.getInstance().getNumceps() + "\n" + 
                    "ENORMALISE = T\n" + 
                    "# Input file format\n" + 
                    "SOURCEKIND = WAVEFORM\n" + 
                    "SOURCEFORMAT = WAVE\n");
            bufferedWriter.close();
            writer.close();
        } catch (IOException ex) {
            logger.error("Error while building HTK configuration", ex);
        }
    }

}
