package com.hedgehogsmind.springcouchrest.workers.discovery.exceptions;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;

/**
 * Shall be thrown, whenever a bean is annotated with {@link CouchRest}
 * whose type is not supported by CouchRest.
 */
public class UnsupportedBeanTypeTaggedException
        extends RuntimeException {

    public UnsupportedBeanTypeTaggedException() {
    }

    public UnsupportedBeanTypeTaggedException(String message) {
        super(message);
    }

    public UnsupportedBeanTypeTaggedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedBeanTypeTaggedException(Throwable cause) {
        super(cause);
    }

    public UnsupportedBeanTypeTaggedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
