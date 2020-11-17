package com.hedgehogsmind.springcouch2r.beans.exceptions;

/**
 * Shall be thrown, if you encounter, that there is already a mapping for the same same path.
 */
public class Couch2rResourcePathClashException extends RuntimeException {

    public Couch2rResourcePathClashException() {
    }

    public Couch2rResourcePathClashException(String message) {
        super(message);
    }

    public Couch2rResourcePathClashException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rResourcePathClashException(Throwable cause) {
        super(cause);
    }

    public Couch2rResourcePathClashException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
