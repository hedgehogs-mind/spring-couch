package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines the interaction points between mapping handlers/implementation and
 * the {@link com.hedgehogsmind.springcouch2r.beans.Couch2rHandlerMapping}, which uses
 * the method {@link #accepts(HttpServletRequest)} to check if the handler can handle the given request, and
 * {@link com.hedgehogsmind.springcouch2r.beans.Couch2rHandlerAdapter}, which uses
 * {@link #handle(HttpServletRequest, ObjectMapper)} to perform the request handling.
 */
public interface MappingHandler {

    /**
     * Checks if it can handle the given request via {@link #handle(HttpServletRequest, ObjectMapper)}.
     *
     * @param request Request to evaluate.
     * @return True if this mapping handler can handle the given request via
     * {@link #handle(HttpServletRequest, ObjectMapper)}, false if not.
     */
    boolean accepts(
            HttpServletRequest request
    );

    /**
     * Handles the given request. This method assumes, that the given request has been passed
     * to {@link #accepts(HttpServletRequest)} and it having returned true prior to this call.
     *
     * @param request      Request to handle.
     * @param objectMapper ObjectMapper to use for JSON (de-) serialization.
     * @return Result as ResponseEntity.
     */
    ResponseEntity handle(
            HttpServletRequest request,
            ObjectMapper objectMapper
    );

}
