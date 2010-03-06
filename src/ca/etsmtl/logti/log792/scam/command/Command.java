package ca.etsmtl.logti.log792.scam.command;

import java.io.OutputStream;

import ca.etsmtl.logti.log792.scam.exception.ScamException;


public interface Command {
    public int execute();

    public void executeAsync();

    public int getExitCode();

    public boolean isRunning();

    public void setOutputStream(OutputStream output) throws ScamException;
}
