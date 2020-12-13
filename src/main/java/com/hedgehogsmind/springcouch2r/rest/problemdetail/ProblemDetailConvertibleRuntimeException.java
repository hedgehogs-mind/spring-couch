package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import java.util.Locale;

/**
 * <p>
 *     Base for all exceptions which can be returned as {@link ProblemDetail}s to the client.
 * </p>
 *
 * <p>
 *     There are some ways to implement {@link #toProblemDetail(Locale)}:
 *     <ul>
 *         <li>Create a new {@link ProblemDetailDto}.</li>
 *         <li>Use a {@link I18nProblemDetailDescriptor} and its method {@link I18nProblemDetailDescriptor#toProblemDetail(Locale)}.</li>
 *         <li>Returns some other instance of type {@link ProblemDetail}.</li>
 *     </ul>
 * </p>
 */
public abstract class ProblemDetailConvertibleRuntimeException extends RuntimeException implements I18nProblemDetailConvertible {

    public ProblemDetailConvertibleRuntimeException() {
    }

    public ProblemDetailConvertibleRuntimeException(String message) {
        super(message);
    }

    public ProblemDetailConvertibleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemDetailConvertibleRuntimeException(Throwable cause) {
        super(cause);
    }

    public ProblemDetailConvertibleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
