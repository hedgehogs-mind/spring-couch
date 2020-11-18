package com.hedgehogsmind.springcouch2r.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DummyTestCouch2rConfiguration implements Couch2rConfiguration {

    @Override
    public String getCouch2rBasePath() {
        return "/testing/couch2r/";
    }

    @Override
    public Optional<ObjectMapper> getCouch2rObjectMapper() {
        return Optional.empty();
    }
}
