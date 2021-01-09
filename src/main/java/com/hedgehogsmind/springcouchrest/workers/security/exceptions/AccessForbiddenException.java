package com.hedgehogsmind.springcouchrest.workers.security.exceptions;

import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;

import java.util.Locale;

/**
 * Shall be thrown in case any security rules evaluate to false.
 */
public class AccessForbiddenException extends ProblemDetailConvertibleRuntimeException {

    public AccessForbiddenException() {
    }

    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessForbiddenException(Throwable cause) {
        super(cause);
    }

    public AccessForbiddenException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public ProblemDetail toProblemDetail(Locale locale) {
        return CouchRestProblems.FORBIDDEN.toProblemDetail(locale);
    }

}
