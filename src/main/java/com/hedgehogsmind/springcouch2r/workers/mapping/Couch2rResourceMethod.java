package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Resembles one method/action within a {@link Couch2rMappedResource}.
 */
public interface Couch2rResourceMethod {

    /**
     * Handles one method. Must check if it can consume/handle the given request. If this
     * resource method is not responsible for the request, the implementation will return Optional.empty().
     *
     * @param request Request.
     * @param objectMapper ObjectMapper for JSON stuff.
     * @param locale Locale of request.
     * @param pathVariables Path variables after {@link Couch2rMappedResource#getFullPath()}.
     * @param queryParameters Query parameters.
     * @return ResponseEntity or empty if next method shall be checked/this one can not handle the request.
     */
    Optional<ResponseEntity> handle(
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Locale locale,
            String method,
            String[] pathVariables,
            Map<String, String[]> queryParameters
    );

}
