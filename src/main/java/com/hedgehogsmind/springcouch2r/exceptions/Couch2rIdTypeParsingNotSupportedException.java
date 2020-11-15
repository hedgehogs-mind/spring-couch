package com.hedgehogsmind.springcouch2r.exceptions;

/**
 * Shall be thrown if parsing an entity's id type is not supported.
 */
public class Couch2rIdTypeParsingNotSupportedException extends RuntimeException {

    public Couch2rIdTypeParsingNotSupportedException() {
    }

    public Couch2rIdTypeParsingNotSupportedException(String message) {
        super(message);
    }

    public Couch2rIdTypeParsingNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rIdTypeParsingNotSupportedException(Throwable cause) {
        super(cause);
    }

    public Couch2rIdTypeParsingNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
