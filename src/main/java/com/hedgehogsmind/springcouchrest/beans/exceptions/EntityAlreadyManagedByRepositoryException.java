package com.hedgehogsmind.springcouchrest.beans.exceptions;

/**
 * Shall be thrown, if there is a {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest}
 * annotated entity, but also a
 * {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest} annotated repository
 * with the same entity type.
 */
public class EntityAlreadyManagedByRepositoryException
        extends RuntimeException {

    public EntityAlreadyManagedByRepositoryException() {
    }

    public EntityAlreadyManagedByRepositoryException(String message) {
        super(message);
    }

    public EntityAlreadyManagedByRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityAlreadyManagedByRepositoryException(Throwable cause) {
        super(cause);
    }

    public EntityAlreadyManagedByRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
