package com.hedgehogsmind.springcouchrest.beans.exceptions;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;

/**
 * Shall be thrown if no unique {@link CouchRestConfiguration}
 * bean could have been found.
 */
public class NoUniqueConfigurationFoundException
        extends RuntimeException {

    public NoUniqueConfigurationFoundException() {
    }

    public NoUniqueConfigurationFoundException(String message) {
        super(message);
    }

    public NoUniqueConfigurationFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoUniqueConfigurationFoundException(Throwable cause) {
        super(cause);
    }

    public NoUniqueConfigurationFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
