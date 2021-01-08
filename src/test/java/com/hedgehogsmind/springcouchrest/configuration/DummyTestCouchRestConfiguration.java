package com.hedgehogsmind.springcouchrest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DummyTestCouchRestConfiguration
        implements CouchRestConfiguration {

    @Override
    public String getCouchRestBasePath() {
        return "/testing/couchrest/";
    }

    @Override
    public Optional<ObjectMapper> getCouchRestObjectMapper() {
        return Optional.empty();
    }
}
