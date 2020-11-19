package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Simple class to describe a problem using i18n keys.
 */
public final class ProblemDescriptor {

    public static final String RESOURCE_BUNDLE_BASE_NAME = "springcouch2r/i18n/problem_detail";

    public static final ResourceBundle.Control RESOURCE_BUNDLE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return Locale.ENGLISH;
        }
    };

    private final String problemType;

    private final String titleKey;

    private final String messageKey;

    private final int status;

    public ProblemDescriptor(String problemType, String titleKey, String messageKey, int status) {
        this.problemType = problemType;
        this.titleKey = titleKey;
        this.messageKey = messageKey;
        this.status = status;
    }

    public String getProblemType() {
        return problemType;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemDescriptor that = (ProblemDescriptor) o;
        return status == that.status &&
                Objects.equals(problemType, that.problemType) &&
                Objects.equals(titleKey, that.titleKey) &&
                Objects.equals(messageKey, that.messageKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemType, titleKey, messageKey, status);
    }

    @Override
    public String toString() {
        return "ProblemDescriptor{" +
                "problemType='" + problemType + '\'' +
                ", titleKey='" + titleKey + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Fetches resource bundle for given locale.
     *
     * @param locale Locale to get bundle for.
     * @return ResourceBundle.
     */
    public static ResourceBundle getResourceBundle(final Locale locale) {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale, RESOURCE_BUNDLE_CONTROL);
    }

    /**
     * Creates new ProblemDetail instance using problem type and status. Title and detail
     * are fetched from the resource bundle using the keys stored in this.
     *
     * @param locale Locale to create instance for.
     * @return Localized ProblemDetail instance.
     */
    public ProblemDetail toProblemDetail(final Locale locale) {
        final ResourceBundle resourceBundle = getResourceBundle(locale);

        return ProblemDetail.byUrn(
                problemType,
                resourceBundle.getString(titleKey),
                resourceBundle.getString(messageKey),
                status
        );
    }

    /**
     * Same as {@link #toProblemDetail(Locale)}, but detail is set to throwable's message. If that
     * is empty, detail is fetched from resource bundle.
     *
     * @param locale Locale to create instance for.
     * @param throwable Throwable to get detail message from.
     * @return Localized ProblemDetail instance from.
     */
    public ProblemDetail toProblemDetail(final Locale locale, final Throwable throwable) {
        final ResourceBundle resourceBundle = getResourceBundle(locale);

        return ProblemDetail.byUrn(
                problemType,
                resourceBundle.getString(titleKey),
                throwable.getLocalizedMessage(),
                status
        );
    }

}
