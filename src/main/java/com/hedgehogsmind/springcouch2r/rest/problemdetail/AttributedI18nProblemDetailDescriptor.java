package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * This class wraps a {@link I18nProblemDetailDescriptor} and allows adding further
 * attributes. They will be populated onto the {@link ProblemDetailDto} instance
 * within {@link #toProblemDetail(Locale)}.
 */
public class AttributedI18nProblemDetailDescriptor implements I18nProblemDetailConvertible {

    private final I18nProblemDetailDescriptor descriptor;

    private final Map<String, Object> data;

    /**
     * Stores descriptor to wrap and initializes an empty map for the attributes.
     * @param descriptor Descriptor to wrap.
     */
    public AttributedI18nProblemDetailDescriptor(I18nProblemDetailDescriptor descriptor) {
        this.descriptor = descriptor;
        this.data = new HashMap<>();
    }

    /**
     * Adds an attribute to the internal map. It will later be populated onto the {@link ProblemDetailDto}
     * instance in {@link #toProblemDetail(Locale)}.
     *
     * @param key Key.
     * @param value Value.
     * @return This for builder like pattern.
     * @throws IllegalStateException if there is already an attribute with the same key.
     */
    public AttributedI18nProblemDetailDescriptor addAttribute(final String key, final Object value) {
        if ( this.data.containsKey(key) ) {
            throw new IllegalStateException("Attribute '"+key+"' has already been set.");
        }

        this.data.put(key, value);
        return this;
    }

    /**
     * Calls {@link I18nProblemDetailDescriptor#toProblemDetail(Locale)} and adds
     * the internal data map to the dto.
     *
     * @param locale Locale for which to localize the messages.
     * @return Problem detail.
     */
    @Override
    public ProblemDetailDto toProblemDetail(Locale locale) {
        final ProblemDetailDto problemDetailDto = descriptor.toProblemDetail(locale);
        problemDetailDto.setData(this.data);

        return problemDetailDto;
    }

    public I18nProblemDetailDescriptor getDescriptor() {
        return descriptor;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributedI18nProblemDetailDescriptor that = (AttributedI18nProblemDetailDescriptor) o;
        return Objects.equals(descriptor, that.descriptor) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, data);
    }

    @Override
    public String toString() {
        return "AttributedI18nProblemDetailDescriptor{" +
                "descriptor=" + descriptor +
                ", data=" + data +
                '}';
    }
}
