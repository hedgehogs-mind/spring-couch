package com.hedgehogsmind.springcouchrest.workers.mapping.entity.exceptions;

import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;

import java.util.Locale;

/**
 * Shall be thrown if parsing an entity's id type is not supported.
 */
public class IdTypeParsingNotSupportedException
        extends ProblemDetailConvertibleRuntimeException {

    private final Class<?> unsupportedType;

    public IdTypeParsingNotSupportedException(Class<?> unsupportedType) {
        this.unsupportedType = unsupportedType;
    }

    public IdTypeParsingNotSupportedException(String message, Class<?> unsupportedType) {
        super(message);
        this.unsupportedType = unsupportedType;
    }

    public IdTypeParsingNotSupportedException(String message, Throwable cause, Class<?> unsupportedType) {
        super(message, cause);
        this.unsupportedType = unsupportedType;
    }

    public IdTypeParsingNotSupportedException(Throwable cause, Class<?> unsupportedType) {
        super(cause);
        this.unsupportedType = unsupportedType;
    }

    public IdTypeParsingNotSupportedException(String message,
                                              Throwable cause,
                                              boolean enableSuppression,
                                              boolean writableStackTrace,
                                              Class<?> unsupportedType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.unsupportedType = unsupportedType;
    }

    @Override
    public ProblemDetail toProblemDetail(Locale locale) {
        return CouchRestProblems.ID_TYPE_PARSING_NOT_SUPPORTED
                .withAttributes()
                .addAttribute("unsupportedType", unsupportedType.getSimpleName())
                .toProblemDetail(locale);
    }
}
