package com.hedgehogsmind.springcouchrest.rest.problemdetail;


import java.util.Locale;

/**
 * Extends the default problem detail convertible contract by a method which needs a locale.
 * {@link #toProblemDetail()} is overridden to call {@link #toProblemDetail(Locale)} with
 * {@link Locale#ENGLISH} as the default locale.
 */
public interface I18nProblemDetailConvertible extends ProblemDetailConvertible {

    /**
     * Calls {@link #toProblemDetail(Locale)} and passes {@link Locale#ENGLISH} as locale.
     * @return ProblemDetail localized for english.
     */
    @Override
    default ProblemDetail toProblemDetail() {
        return this.toProblemDetail(Locale.ENGLISH);
    }

    /**
     * Produces problem detail instance localized for the given locale.
     *
     * @param locale Locale to localize messages etc. for.
     * @return
     */
    ProblemDetail toProblemDetail(Locale locale);

}
