package com.hedgehogsmind.springcouchrest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * Default implementation intended for out of the box use or extension where necessary.
 */
public class CouchRestConfigurationAdapter implements CouchRestConfiguration {

    @Override
    public String getCouchRestBasePath() {
        return "/api/";
    }

    @Override
    public Optional<ObjectMapper> getCouchRestObjectMapper() {
        return Optional.empty();
    }

    @Override
    public String getBaseSecurityRule() {
        return "denyAll()";
    }

    @Override
    public Optional<Object> getSpringElEvaluationRootObject() {
        return Optional.empty();
    }
}
