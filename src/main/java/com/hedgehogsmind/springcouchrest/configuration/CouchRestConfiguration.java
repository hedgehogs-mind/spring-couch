package com.hedgehogsmind.springcouchrest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public interface CouchRestConfiguration {

    /**
     * Shall return the base path under which the resources shall be made available.
     * Must end with trailing slash.
     *
     * @return Base path of resources published (with trailing slash).
     */
    String getCouchRestBasePath();

    /**
     * Might return an own ObjectMapper (configuration). If not either the global ObjectMapper
     * is used or a new ones will be created.
     *
     * @return Optional ObjectMapper.
     */
    Optional<ObjectMapper> getCouchRestObjectMapper();

}
