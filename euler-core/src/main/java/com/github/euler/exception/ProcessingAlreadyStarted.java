package com.github.euler.exception;

public class ProcessingAlreadyStarted extends Exception {

    private static final long serialVersionUID = 1L;

    public ProcessingAlreadyStarted() {
        super();
    }

    public ProcessingAlreadyStarted(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ProcessingAlreadyStarted(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingAlreadyStarted(String message) {
        super(message);
    }

    public ProcessingAlreadyStarted(Throwable cause) {
        super(cause);
    }

}
