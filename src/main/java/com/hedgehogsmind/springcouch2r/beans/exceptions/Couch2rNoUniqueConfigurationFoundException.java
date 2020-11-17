package com.hedgehogsmind.springcouch2r.beans.exceptions;

/**
 * Shall be thrown if no unique {@link com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration}
 * bean could have been found.
 */
public class Couch2rNoUniqueConfigurationFoundException extends RuntimeException {

    public Couch2rNoUniqueConfigurationFoundException() {
    }

    public Couch2rNoUniqueConfigurationFoundException(String message) {
        super(message);
    }

    public Couch2rNoUniqueConfigurationFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rNoUniqueConfigurationFoundException(Throwable cause) {
        super(cause);
    }

    public Couch2rNoUniqueConfigurationFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
