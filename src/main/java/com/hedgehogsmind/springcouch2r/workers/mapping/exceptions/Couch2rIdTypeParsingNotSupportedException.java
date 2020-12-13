package com.hedgehogsmind.springcouch2r.workers.mapping.exceptions;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;

import java.util.Locale;

/**
 * Shall be thrown if parsing an entity's id type is not supported.
 */
public class Couch2rIdTypeParsingNotSupportedException extends ProblemDetailConvertibleRuntimeException {

    private final Class<?> unsupportedType;

    public Couch2rIdTypeParsingNotSupportedException(Class<?> unsupportedType) {
        this.unsupportedType = unsupportedType;
    }

    public Couch2rIdTypeParsingNotSupportedException(String message, Class<?> unsupportedType) {
        super(message);
        this.unsupportedType = unsupportedType;
    }

    public Couch2rIdTypeParsingNotSupportedException(String message, Throwable cause, Class<?> unsupportedType) {
        super(message, cause);
        this.unsupportedType = unsupportedType;
    }

    public Couch2rIdTypeParsingNotSupportedException(Throwable cause, Class<?> unsupportedType) {
        super(cause);
        this.unsupportedType = unsupportedType;
    }

    public Couch2rIdTypeParsingNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Class<?> unsupportedType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.unsupportedType = unsupportedType;
    }

    @Override
    public ProblemDetail toProblemDetail(Locale locale) {
        return Couch2rProblems.ID_TYPE_PARSING_NOT_SUPPORTED
                .withAttributes()
                .addAttribute("unsupportedType", unsupportedType.getSimpleName())
                .toProblemDetail(locale);
    }
}
