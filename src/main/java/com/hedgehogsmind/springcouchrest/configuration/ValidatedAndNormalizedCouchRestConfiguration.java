package com.hedgehogsmind.springcouchrest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.util.PathUtil;

import java.util.Optional;

/**
 * Takes an other {@link CouchRestConfiguration}, validates data and copies normalized values.
 */
public class ValidatedAndNormalizedCouchRestConfiguration
        implements CouchRestConfiguration {

    private final String basePath;

    private final Optional<ObjectMapper> objectMapper;

    private final String baseSecurityRule;

    private final Optional<Object> springElEvaluationRootObject;

    /**
     * Copies data and normalizes it if necessary.
     * @param original Original configuration.
     */
    public ValidatedAndNormalizedCouchRestConfiguration(final CouchRestConfiguration original) {
        this.basePath = validateAndNormalizeBasePath(original.getCouchRestBasePath());
        this.objectMapper = original.getCouchRestObjectMapper();
        this.baseSecurityRule = validateBaseSecurityRule(original.getBaseSecurityRule());
        this.springElEvaluationRootObject = original.getSpringElEvaluationRootObject();
    }

    /**
     * Checks that the basePath is neither null or empty. Replaces double slashes by single one and returns
     * path with trailing slash.
     * @param basePath BasePath to validate and normalize.
     * @return basePath with trailing slash.
     */
    public String validateAndNormalizeBasePath(final String basePath) {
        if ( basePath == null ) throw new IllegalArgumentException("CouchRest basePath must not be null");
        if ( basePath.isBlank() ) throw new IllegalArgumentException("CouchRest basePath must not be empty");
        if ( !basePath.endsWith("/") ) throw new IllegalArgumentException("CouchRest basePath must end with trailing slash");

        return PathUtil.removeMultipleSlashes(basePath);
    }

    /**
     * Checks that the given rule is not empty.
     * @param baseSecurityRule Rule to check.
     * @return Rule.
     */
    public String validateBaseSecurityRule(final String baseSecurityRule) {
        if ( baseSecurityRule == null || baseSecurityRule.isBlank() ) {
            throw new IllegalArgumentException("CouchRest baseSecurityRule must not be empty");
        }

        return baseSecurityRule;
    }

    @Override
    public String getCouchRestBasePath() {
        return basePath;
    }

    @Override
    public Optional<ObjectMapper> getCouchRestObjectMapper() {
        return objectMapper;
    }

    @Override
    public String getBaseSecurityRule() {
        return baseSecurityRule;
    }

    @Override
    public Optional<Object> getSpringElEvaluationRootObject() {
        return springElEvaluationRootObject;
    }
}
