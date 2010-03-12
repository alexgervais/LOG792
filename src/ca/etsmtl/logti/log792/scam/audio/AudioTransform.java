package ca.etsmtl.logti.log792.scam.audio;

import java.io.File;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.command.impl.CommandImpl;
import ca.etsmtl.logti.log792.scam.config.Config;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class AudioTransform {
    
    private File input;
    
    public AudioTransform(File audioInput) {
        input = audioInput;
    }
    
    public File transform() throws ScamException {
        File audioFile = new File(Config.getInstance().getAudioFolder(), input.getName() + ".wav");
        if (!audioFile.exists()) {
            if (!Config.getInstance().getAudioFolder().exists()) {
                Config.getInstance().getAudioFolder().mkdirs();
            }
            String[] command = { Config.getInstance().getSox() + "sox", "--volume", "0.9", input.getAbsolutePath(), "--bits", "16", "--channels", "1", "--encoding", "signed-integer", "--rate", "32k", "--endian", "little", audioFile.getAbsolutePath() };
            Command c = new CommandImpl(command);
            if (c.execute() != 0) {
                throw new ScamException("Error transforming audio file");
            }
        }
        return audioFile;
    }
}
