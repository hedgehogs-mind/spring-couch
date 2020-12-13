package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * This class is a POJO implementation of {@link ProblemDetail}.
 */
public class ProblemDetailDto implements ProblemDetail {

    private URI type;

    private String title;

    private String detail;

    private int status;

    private URI instance;

    private Map<String, Object> data;

    public ProblemDetailDto() {
    }

    public ProblemDetailDto(URI type, String title, String detail, int status, URI instance) {
        this(type, title, detail, status, instance, null);
    }

    public ProblemDetailDto(URI type, String title, String detail, int status, URI instance, Map<String, Object> data) {
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.instance = instance;
        this.data = data;
    }

    public ProblemDetailDto(String typeAsString, String title, String detail, int status, URI instance) {
        this(
                URI.create("urn:problem-type:"+typeAsString),
                title,
                detail,
                status,
                instance
        );
    }

    public ProblemDetailDto(String typeAsString, String title, String detail, int status, URI instance, Map<String, Object> data) {
        this(
                URI.create("urn:problem-type:"+typeAsString),
                title,
                detail,
                status,
                instance,
                data
        );
    }

    @Override
    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemDetailDto that = (ProblemDetailDto) o;
        return status == that.status && Objects.equals(type, that.type) && Objects.equals(title, that.title) && Objects.equals(detail, that.detail) && Objects.equals(instance, that.instance) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, detail, status, instance, data);
    }

    @Override
    public String toString() {
        return "ProblemDetailDto{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", status=" + status +
                ", instance=" + instance +
                ", data=" + data +
                '}';
    }

}
