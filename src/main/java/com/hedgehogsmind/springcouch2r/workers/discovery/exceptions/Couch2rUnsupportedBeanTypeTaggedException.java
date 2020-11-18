package com.hedgehogsmind.springcouch2r.workers.discovery.exceptions;

/**
 * Shall be thrown, whenever a bean is annotated with {@link com.hedgehogsmind.springcouch2r.annotations.Couch2r}
 * whose type is not supported by Couch2r.
 */
public class Couch2rUnsupportedBeanTypeTaggedException extends RuntimeException {

    public Couch2rUnsupportedBeanTypeTaggedException() {
    }

    public Couch2rUnsupportedBeanTypeTaggedException(String message) {
        super(message);
    }

    public Couch2rUnsupportedBeanTypeTaggedException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rUnsupportedBeanTypeTaggedException(Throwable cause) {
        super(cause);
    }

    public Couch2rUnsupportedBeanTypeTaggedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
