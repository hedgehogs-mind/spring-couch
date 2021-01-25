package com.hedgehogsmind.springcouchrest.data.discovery.exceptions;

/**
 * This exception shall be thrown if a mapping needs a certain annotation, but it was not there.
 */
public class RequiredCouchRestModifierAnnotationNotFoundException
        extends RuntimeException {

    public RequiredCouchRestModifierAnnotationNotFoundException() {
    }

    public RequiredCouchRestModifierAnnotationNotFoundException(String message) {
        super(message);
    }

    public RequiredCouchRestModifierAnnotationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequiredCouchRestModifierAnnotationNotFoundException(Throwable cause) {
        super(cause);
    }

    public RequiredCouchRestModifierAnnotationNotFoundException(String message,
                                                                Throwable cause,
                                                                boolean enableSuppression,
                                                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
