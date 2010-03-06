package ca.etsmtl.logti.log792.scam.exception;

public class ScamException extends Exception {

    private static final long serialVersionUID = 1L;

    public ScamException(String message) {
        super(message);
    }
    
    public ScamException(Throwable cause) {
        super(cause);
    }
    
    public ScamException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
