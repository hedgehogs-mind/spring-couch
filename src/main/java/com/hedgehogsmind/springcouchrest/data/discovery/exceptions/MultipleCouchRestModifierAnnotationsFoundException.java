package com.hedgehogsmind.springcouchrest.data.discovery.exceptions;

/**
 * This exception shall be thrown if a mapping needs no or one annotation instance of a certain
 * annotation type, but there were at least two which leads to an unambiguous state.
 */
public class MultipleCouchRestModifierAnnotationsFoundException extends RuntimeException {

    public MultipleCouchRestModifierAnnotationsFoundException() {
    }

    public MultipleCouchRestModifierAnnotationsFoundException(String message) {
        super(message);
    }

    public MultipleCouchRestModifierAnnotationsFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleCouchRestModifierAnnotationsFoundException(Throwable cause) {
        super(cause);
    }

    public MultipleCouchRestModifierAnnotationsFoundException(String message,
                                                              Throwable cause,
                                                              boolean enableSuppression,
                                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
