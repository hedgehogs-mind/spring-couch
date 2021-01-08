package com.hedgehogsmind.springcouchrest.beans.exceptions;

/**
 * Shall be thrown, if you encounter, that there is already a mapping for the same same path.
 */
public class ResourcePathClashException
        extends RuntimeException {

    public ResourcePathClashException() {
    }

    public ResourcePathClashException(String message) {
        super(message);
    }

    public ResourcePathClashException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourcePathClashException(Throwable cause) {
        super(cause);
    }

    public ResourcePathClashException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
