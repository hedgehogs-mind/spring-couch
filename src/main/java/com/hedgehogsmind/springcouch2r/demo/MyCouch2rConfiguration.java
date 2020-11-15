package com.hedgehogsmind.springcouch2r.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyCouch2rConfiguration implements Couch2rConfiguration {

    @Override
    public String getCouch2rBasePath() {
        return "/api/couch2r";
    }

    @Override
    public Optional<ObjectMapper> getCouch2rObjectMapper() {
        return Optional.empty();
    }
}
