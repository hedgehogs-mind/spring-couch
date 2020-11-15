package com.hedgehogsmind.springcouch2r.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public interface Couch2rConfiguration {

    /**
     * Shall return the base path under which the resources shall be made available.
     *
     * @return Base path of resources published.
     */
    String getCouch2rBasePath();

    /**
     * Might return an own ObjectMapper (configuration). If not either the global ObjectMapper
     * is used or a new ones will be created.
     *
     * @return Optional ObjectMapper.
     */
    Optional<ObjectMapper> getCouch2rObjectMapper();

}
