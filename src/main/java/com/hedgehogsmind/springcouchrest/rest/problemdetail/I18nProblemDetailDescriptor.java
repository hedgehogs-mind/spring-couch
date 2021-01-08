package com.hedgehogsmind.springcouchrest.rest.problemdetail;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * This class represents a blue print for problem detail with i18n messages.
 * Title and detail must be specified by i18n message keys.
 *
 * On {@link #toProblemDetail(Locale)} the keys will be resolved to messages.
 */
public class I18nProblemDetailDescriptor implements I18nProblemDetailConvertible {

    public static final String RESOURCE_BUNDLE_BASE_NAME = "springcouchrest/i18n/problem_detail";

    public static final ResourceBundle.Control RESOURCE_BUNDLE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return Locale.ENGLISH;
        }
    };

    private final URI type;

    private final String titleKey;

    private final String detailKey;

    private final int status;

    /**
     * Stores data.
     * @param type Type.
     * @param titleKey I18n title key.
     * @param detailKey I18n detail key.
     * @param status HTTP status.
     */
    public I18nProblemDetailDescriptor(URI type, String titleKey, String detailKey, int status) {
        this.type = type;
        this.titleKey = titleKey;
        this.detailKey = detailKey;
        this.status = status;
    }

    /**
     * Convenience constructor. Calls {@link #I18nProblemDetailDescriptor(URI, String, String, int)}
     * and creates a new URI with the textual prefix "urn:problem-type:" and appends the given type string to it.
     *
     * @param typeAsString Type string to use for urn problem type.
     * @param titleKey I18n title key.
     * @param detailKey I18n detail key.
     * @param status HTTP status.
     */
    public I18nProblemDetailDescriptor(final String typeAsString, String titleKey, String detailKey, int status) {
        this(
                URI.create("urn:problem-type:"+typeAsString),
                titleKey,
                detailKey,
                status
        );
    }

    public URI getType() {
        return type;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getDetailKey() {
        return detailKey;
    }

    public int getStatus() {
        return status;
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
     * Creates a {@link ProblemDetailDto} by resolving the message keys and generating a urn uuid instance.
     *
     * @param locale Locale for which the messages shall be resolved.
     * @return ProblemDetail with resolved messages.
     */
    @Override
    public ProblemDetailDto toProblemDetail(Locale locale) {
        final ResourceBundle resourceBundle = getResourceBundle(locale);

        return new ProblemDetailDto(
                type,
                resourceBundle.getString(titleKey),
                resourceBundle.getString(detailKey),
                status,
                URI.create("urn:uuid:"+UUID.randomUUID())
        );
    }

    /**
     * Wraps this in a new {@link AttributedI18nProblemDetailDescriptor} instance.
     *
     * @return New attributed wrapper.
     */
    public AttributedI18nProblemDetailDescriptor withAttributes() {
        return new AttributedI18nProblemDetailDescriptor(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18nProblemDetailDescriptor that = (I18nProblemDetailDescriptor) o;
        return status == that.status && Objects.equals(type, that.type) && Objects.equals(titleKey, that.titleKey) && Objects.equals(detailKey, that.detailKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, titleKey, detailKey, status);
    }

    @Override
    public String toString() {
        return "I18nProblemDetailDescriptor{" +
                "type=" + type +
                ", titleKey='" + titleKey + '\'' +
                ", detailKey='" + detailKey + '\'' +
                ", status=" + status +
                '}';
    }

}
