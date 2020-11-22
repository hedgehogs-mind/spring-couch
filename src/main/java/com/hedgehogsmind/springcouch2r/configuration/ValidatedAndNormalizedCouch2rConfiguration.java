package com.hedgehogsmind.springcouch2r.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.util.Couch2rPathUtil;

import java.util.Optional;

/**
 * Takes an other {@link Couch2rConfiguration}, validates data and copies normalized values.
 */
public class ValidatedAndNormalizedCouch2rConfiguration implements Couch2rConfiguration {

    private final String basePath;

    private final Optional<ObjectMapper> objectMapper;

    /**
     * Copies data and normalizes it if necessary.
     * @param original Original configuration.
     */
    public ValidatedAndNormalizedCouch2rConfiguration(final Couch2rConfiguration original) {
        this.basePath = validateAndNormalizeBasePath(original.getCouch2rBasePath());
        this.objectMapper = original.getCouch2rObjectMapper();
    }

    /**
     * Checks that the basePath is neither null or empty. Replaces double slashes by single one and returns
     * path with trailing slash.
     * @param basePath BasePath to validate and normalize.
     * @return basePath with trailing slash.
     */
    public String validateAndNormalizeBasePath(final String basePath) {
        if ( basePath == null ) throw new IllegalArgumentException("Couch2r basePath must not be null");
        if ( basePath.isBlank() ) throw new IllegalArgumentException("Couch2r basePath must not be empty");
        if ( !basePath.endsWith("/") ) throw new IllegalArgumentException("Couch2r basePath must end with trailing slash");

        return Couch2rPathUtil.removeMultipleSlashes(basePath);
    }

    @Override
    public String getCouch2rBasePath() {
        return basePath;
    }

    @Override
    public Optional<ObjectMapper> getCouch2rObjectMapper() {
        return objectMapper;
    }
}
