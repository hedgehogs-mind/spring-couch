package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hedgehogsmind.springcouch2r.rest.ResponseEntityConvertible;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

/**
 * This interface defines a contract for data a problem detail implementation must deliver.
 * This refers to RFC 7807.
 */
@JsonSerialize(as = ProblemDetail.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProblemDetail extends ResponseEntityConvertible<ProblemDetail> {

    MediaType CONTENT_TYPE = MediaType.parseMediaType("application/problem+json");

    ObjectMapper PROBLEM_DETAIL_MAPPER = new ObjectMapper();

    /**
     * Returns the problem type. May refer to a documentation web page or represents
     * a problem type using a URN like "urn:problem-type:...".
     *
     * @return Returns problem type as URI.
     */
    URI getType();

    /**
     * Title of the problem.
     *
     * @return Problem title.
     */
    String getTitle();

    /**
     * Delivers a more in depth explanation of the title.
     *
     * @return In depth explanation.
     */
    String getDetail();

    /**
     * Represents the HTTP status.
     *
     * @return HTTP status.
     */
    int getStatus();

    /**
     * Represents the semantic problem instance using a URI. May be a URN with a UUID like
     * "urn:uuid:..." or anything else. It is useful to find the problem in logs or error
     * databases.
     *
     * @return Problem instance.
     */
    URI getInstance();

    /**
     * Optional data to be embedded in the response.
     *
     * @return Some additional data. Can be null.
     */
    @JsonAnyGetter
    Map<String, Object> getData();

    /**
     * Uses {@link #PROBLEM_DETAIL_MAPPER} to serialize this problem detail instance as JSON.
     *
     * @return JSON.
     */
    default String toJson() {
        try {
            return PROBLEM_DETAIL_MAPPER.writeValueAsString(this);
        } catch ( JsonProcessingException e ) {
            throw new RuntimeException("Could not serialize ProblemDetail as JSON");
        }
    }

    /**
     * Creates new ResponseEntity by setting status to {@link #getStatus()} and the body to this.
     *
     * @return ResponseEntity carrying this ProblemDetail.
     */
    @Override
    default ResponseEntity<ProblemDetail> toResponseEntity() {
        return ResponseEntity.status(getStatus()).body(this);
    }
}
