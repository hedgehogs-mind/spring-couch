package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import com.hedgehogsmind.springcouch2r.rest.ResponseEntityConvertible;
import org.springframework.http.ResponseEntity;

/**
 * Represents class contract to be able to deliver a ProblemDetail instance.
 */
public interface ProblemDetailConvertible extends ResponseEntityConvertible {

    /**
     * Produces a problem detail instance.
     * @return Problem detail instance.
     */
    ProblemDetail toProblemDetail();

    /**
     * Wraps itself into response entity.
     * @return ResponseEntity carrying this convertible.
     */
    @Override
    default ResponseEntity toResponseEntity() {
        return ResponseEntity.ok(this);
    }
}
