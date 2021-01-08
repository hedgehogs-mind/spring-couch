package com.hedgehogsmind.springcouchrest.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyCouchRestConfiguration
        implements CouchRestConfiguration {

    @Override
    public String getCouchRestBasePath() {
        return "/api/couchrest/";
    }

    @Override
    public Optional<ObjectMapper> getCouchRestObjectMapper() {
        return Optional.empty();
    }
}
