package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.*;

/**
 * Represents a RFC 7087 ProblemDetail.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProblemDetail {

    public static final MediaType MEDIA_TYPE =
            MediaType.valueOf("application/problem+json");

    public static final ObjectMapper SERIALIZER = new ObjectMapper();

    public static final Set<String> FORBIDDEN_FURTHER_ATTRIBUTE_NAMES = Set.of(
            "type", "title", "detail", "status", "instance"
    );

    private URI type;

    private String title;

    private String detail;

    private int status;

    private URI instance;

    private final Map<String, Object> furtherAttributes = new HashMap<>();

    public ProblemDetail() {
    }

    public ProblemDetail(URI type, String title, String detail, int status, URI instance) {
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.instance = instance;
    }

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    @JsonAnyGetter
    public Map<String, Object> getFurtherAttributes() {
        return furtherAttributes;
    }

    /**
     * Adds an attribute to the map of additional attributes.
     * @param name Name of attribute.
     * @param value Value. Serialized by Jackson.
     * @return This for builder like usage.
     */
    @JsonAnySetter
    public ProblemDetail addAttribute(final String name, final Object value) {
        if ( FORBIDDEN_FURTHER_ATTRIBUTE_NAMES.contains(name) ) {
            throw new IllegalArgumentException("'"+name+"' can not be used as key for further attributes.");
        }

        furtherAttributes.put(name, value);

        return this;
    }

    /**
     * Serializes this ProblemDetail instance as JSON using the static {@link #SERIALIZER}.
     *
     * @return JSON String.
     */
    public String serializeAsJson() {
        try {
            return SERIALIZER.writeValueAsString(this);
        } catch ( JsonProcessingException e ) {
            throw new RuntimeException("Error serializing ProblemDetail", e);
        }
    }

    /**
     * Convenience method. Creates ResponseEntity. Sets
     * @return
     */
    public ResponseEntity<ProblemDetail> toResponseEntity() {
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, ProblemDetail.MEDIA_TYPE.toString())
                .body(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemDetail that = (ProblemDetail) o;
        return status == that.status &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(instance, that.instance) &&
                Objects.equals(furtherAttributes, that.furtherAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, detail, status, instance, furtherAttributes);
    }

    @Override
    public String toString() {
        return "ProblemDetail{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", status=" + status +
                ", instance=" + instance +
                ", furtherAttributes=" + furtherAttributes +
                '}';
    }

    /**
     * <p>
     *     Creates a new ProblemDetail. Problem type is converted into a URI
     *     by prepending the prefix 'urn:problem-type'.
     * </p>
     *
     * <p>
     *     The instance field is set to a URN of a random UUID. So for
     *     example 'urn:uuid:907sad97...'.
     * </p>
     *
     * @param problemType Problem type name/identifier.
     * @param title Title.
     * @param detail Detail.
     * @param status Status code.
     * @return New ProblemDetail instance.
     */
    public static ProblemDetail byUrn(
            final String problemType,
            final String title,
            final String detail,
            final int status
    ) {
        return new ProblemDetail(
                URI.create("urn:problem-type:"+problemType),
                title,
                detail,
                status,
                URI.create("urn:uuid:"+ UUID.randomUUID())
        );
    }

}
