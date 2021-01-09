package com.hedgehogsmind.springcouchrest.beans.exceptions;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;

/**
 * Shall be returned, if {@link CouchRestConfiguration#getDefaultEndpointSecurityRule()}
 * does not return a boolean value.
 */
public class DefaultSecurityRuleDoesNotReturnBooleanValueException extends RuntimeException {

    public DefaultSecurityRuleDoesNotReturnBooleanValueException() {
    }

    public DefaultSecurityRuleDoesNotReturnBooleanValueException(String message) {
        super(message);
    }

    public DefaultSecurityRuleDoesNotReturnBooleanValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefaultSecurityRuleDoesNotReturnBooleanValueException(Throwable cause) {
        super(cause);
    }

    public DefaultSecurityRuleDoesNotReturnBooleanValueException(String message,
                                                                 Throwable cause,
                                                                 boolean enableSuppression,
                                                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
