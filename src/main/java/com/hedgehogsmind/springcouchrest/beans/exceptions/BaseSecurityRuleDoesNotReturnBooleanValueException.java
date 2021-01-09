package com.hedgehogsmind.springcouchrest.beans.exceptions;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;

/**
 * Shall be thrown, if {@link CouchRestConfiguration#getBaseSecurityRule()} does not return a boolean value.
 */
public class BaseSecurityRuleDoesNotReturnBooleanValueException extends RuntimeException {

    public BaseSecurityRuleDoesNotReturnBooleanValueException() {
    }

    public BaseSecurityRuleDoesNotReturnBooleanValueException(String message) {
        super(message);
    }

    public BaseSecurityRuleDoesNotReturnBooleanValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseSecurityRuleDoesNotReturnBooleanValueException(Throwable cause) {
        super(cause);
    }

    public BaseSecurityRuleDoesNotReturnBooleanValueException(String message,
                                                              Throwable cause,
                                                              boolean enableSuppression,
                                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
