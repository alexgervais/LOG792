package ca.etsmtl.logti.log792.scam.command.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import ca.etsmtl.logti.log792.scam.command.Command;
import ca.etsmtl.logti.log792.scam.exception.ScamException;



public class CommandImpl implements Command {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    private String[] command = null;
    private int exitCode = -1;
    private boolean running = false;
    private OutputStream standardOutput = System.out;

    private StreamHandler outputHandler = null;

    public CommandImpl(String[] command) {
        this.command = command;
    }

    public int execute() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            setRunning(true);
            if (standardOutput != null) {
                outputHandler = new StreamHandler(proc.getInputStream(), standardOutput);
                outputHandler.start();
            }
            StreamHandler errorHandler = new StreamHandler(proc.getErrorStream(), System.err);
            errorHandler.start();

            setExitCode(proc.waitFor());
            if (outputHandler != null)
                outputHandler.join();
            errorHandler.join();
        } catch (Throwable ex) {
            logger.error(ex);
        } finally {
            setRunning(false);
        }

        logger.info("Exit value [" + getExitCode() + "] command [" + command.toString() + "]");

        return getExitCode();
    }

    public void executeAsync() {
        Thread executor = new Thread () {
            public void run() {
                execute();
            }
        };
        executor.start();
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    private void setExitCode(int code) {
        this.exitCode = code;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setOutputStream(OutputStream output) throws ScamException {
        if (outputHandler != null && outputHandler.isAlive()) {
            throw new ScamException("Process already started, could not attach output stream");
        }
        this.standardOutput = output;
    }

    private class StreamHandler extends Thread {
        private InputStream input;
        private OutputStream redirect;

        public StreamHandler(InputStream inputStream, OutputStream outputStream) {
            this.input = inputStream;
            this.redirect = outputStream;
        }

        public void run() {
            try {
                PrintWriter printWriter = null;
                if (this.redirect != null)
                    printWriter = new PrintWriter(this.redirect);

                InputStreamReader inputStreamReader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (printWriter != null)
                        printWriter.println(line);
                }

                if (printWriter != null)
                    printWriter.flush();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}