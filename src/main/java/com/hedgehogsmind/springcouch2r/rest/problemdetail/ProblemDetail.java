package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.springframework.http.MediaType;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a RFC 7087 ProblemDetail.
 */
public final class ProblemDetail {

    public static final MediaType JSON_MEDIA_TYPE =
            MediaType.valueOf("application/problem+json");

    private URI type;

    private String title;

    private String detail;

    private int status;

    private URI instance;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemDetail that = (ProblemDetail) o;
        return status == that.status &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, detail, status, instance);
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
