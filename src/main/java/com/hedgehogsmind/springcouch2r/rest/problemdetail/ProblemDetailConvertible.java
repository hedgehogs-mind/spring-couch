package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.springframework.http.ResponseEntity;

import java.util.Locale;

/**
 * Implementations are able to convert themself to ProblemDetail instances.
 */
public interface ProblemDetailConvertible {

    /**
     * Creates new ProblemDetail instance.
     *
     * @param locale Locale to create instance for.
     * @return Localized ProblemDetail instance.
     */
    ProblemDetail toProblemDetail(final Locale locale);

    /**
     * Same as {@link #toProblemDetail(Locale)}, but detail is set to throwable's message. If that
     * is empty, detail is fetched from resource bundle.
     *
     * @param locale Locale to create instance for.
     * @param throwable Throwable to get detail message from.
     * @return Localized ProblemDetail instance from.
     */
    ProblemDetail toProblemDetail(final Locale locale, final Throwable throwable);

    /**
     * Implementation shall wrap itself as body and set the status as well as
     * content type header of the ResponseEntity accordingly.
     *
     * @return ResponseEntity.
     */
    ResponseEntity<ProblemDetailConvertible> toResponseEntity();

}
