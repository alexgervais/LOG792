package ca.etsmtl.logti.log792.scam.audio;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.command.impl.CommandImpl;
import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class FeatureExtractor {
    
    private final static Logger logger = Logger.getLogger(FeatureExtractor.class);

    private File audio;

    public FeatureExtractor(File audioInput) {
        audio = audioInput;
    }

    public File extractRawMFCC() throws ScamException {
        File rawMFCC = new File(Config.getInstance().getMfccRawFolder(), audio.getName() + ".mfc");
        if (!rawMFCC.exists()) {
            if (!Config.getInstance().getMfccRawFolder().exists()) {
                Config.getInstance().getMfccRawFolder().mkdirs();
            }
            String[] command = { Config.getInstance().getHtk() + "HCopy", "-C", Config.getInstance().getHtkConfiguration(), audio.getAbsolutePath(), rawMFCC.getAbsolutePath() };
            Command c = new CommandImpl(command);
            if (c.execute() != 0) {
                throw new ScamException("Error extracting raw MFCC...");
            }
        }
        return rawMFCC;
    }

    public File extractTxtMFCC() throws ScamException {
        File textMFCC = new File(Config.getInstance().getMfccTxtFolder(), audio.getName() + ".txt");
        if (!textMFCC.exists()) {
            if (!Config.getInstance().getMfccTxtFolder().exists()) {
                Config.getInstance().getMfccTxtFolder().mkdirs();
            }
            String[] command = { Config.getInstance().getHtk() + "HList", "-i", "100", "-r", extractRawMFCC().getAbsolutePath() };
            Command c = new CommandImpl(command);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(textMFCC);
                c.setOutputStream(fileOutputStream);
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
            if (c.execute() != 0) {
                throw new ScamException("Error extracting txt MFCC...");
            }
        }
        return textMFCC;
    }
}
