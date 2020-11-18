package com.hedgehogsmind.springcouch2r.beans.exceptions;

/**
 * Shall be thrown, if there is a Couch2r annotated entity, but also a Couch2r annotated repository
 * with the same entity type.
 */
public class Couch2rEntityAlreadyManagedByRepositoryException extends RuntimeException {

    public Couch2rEntityAlreadyManagedByRepositoryException() {
    }

    public Couch2rEntityAlreadyManagedByRepositoryException(String message) {
        super(message);
    }

    public Couch2rEntityAlreadyManagedByRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rEntityAlreadyManagedByRepositoryException(Throwable cause) {
        super(cause);
    }

    public Couch2rEntityAlreadyManagedByRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
