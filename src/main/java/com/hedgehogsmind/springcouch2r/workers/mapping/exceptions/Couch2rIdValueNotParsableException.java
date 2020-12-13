package com.hedgehogsmind.springcouch2r.workers.mapping.exceptions;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;

import java.util.Locale;

/**
 * Shall be thrown whenever a String value can not be parsed as an entity's ID value.
 */
public class Couch2rIdValueNotParsableException extends ProblemDetailConvertibleRuntimeException {

    public Couch2rIdValueNotParsableException() {
    }

    public Couch2rIdValueNotParsableException(String message) {
        super(message);
    }

    public Couch2rIdValueNotParsableException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch2rIdValueNotParsableException(Throwable cause) {
        super(cause);
    }

    public Couch2rIdValueNotParsableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public ProblemDetail toProblemDetail(Locale locale) {
        return Couch2rProblems.WRONG_ID_TYPE.toProblemDetail(locale);
    }

}
