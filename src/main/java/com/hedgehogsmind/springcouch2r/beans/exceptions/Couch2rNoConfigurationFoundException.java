package com.hedgehogsmind.springcouch2r.beans.exceptions;

/**
 * Shall be thrown, if no {@link com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration}
 * bean is available.
 */
public class Couch2rNoConfigurationFoundException extends RuntimeException {

    public Couch2rNoConfigurationFoundException() {
    }

    public Couch2rNoConfigurationFoundException(String message) {
        super(message);
    }

    public Couch2rNoConfigurationFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rNoConfigurationFoundException(Throwable cause) {
        super(cause);
    }

    public Couch2rNoConfigurationFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
