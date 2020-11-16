package com.hedgehogsmind.springcouch2r.workers.exceptions;

/**
 * Shall be thrown, whenever a bean is annotated with {@link com.hedgehogsmind.springcouch2r.annotations.Couch2r}
 * whose type is not supported by Couch2r.
 */
public class Couch2rUnsupportedBeanTypeFound extends RuntimeException {

    public Couch2rUnsupportedBeanTypeFound() {
    }

    public Couch2rUnsupportedBeanTypeFound(String message) {
        super(message);
    }

    public Couch2rUnsupportedBeanTypeFound(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rUnsupportedBeanTypeFound(Throwable cause) {
        super(cause);
    }

    public Couch2rUnsupportedBeanTypeFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
