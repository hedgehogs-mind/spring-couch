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

    /**
     * Shall return the global base security rule. It will be applied to all endpoints which
     * have no own overriding security rules. The rule must be defined as a SpringEL expression.
     *
     * @return Base security rule as SpringEL expression.
     */
    String getBaseSecurityRule();

}
