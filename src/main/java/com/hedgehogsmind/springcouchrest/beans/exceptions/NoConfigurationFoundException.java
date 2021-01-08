package com.hedgehogsmind.springcouchrest.beans.exceptions;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;

/**
 * Shall be thrown, if no {@link CouchRestConfiguration}
 * bean is available.
 */
public class NoConfigurationFoundException
        extends RuntimeException {

    public NoConfigurationFoundException() {
    }

    public NoConfigurationFoundException(String message) {
        super(message);
    }

    public NoConfigurationFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConfigurationFoundException(Throwable cause) {
        super(cause);
    }

    public NoConfigurationFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
