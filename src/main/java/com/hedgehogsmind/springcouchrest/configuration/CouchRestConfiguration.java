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
     * before evaluating any endpoint specific rules. You can use most of the Spring Security SpringEL
     * expressions.
     *
     * @return Base security rule as SpringEL expression.
     */
    String getBaseSecurityRule();

    /**
     * Shall return the default security rule that will be used on the endpoint level for all endpoints
     * where no separate security rule has been declared by you. Use a SpringEL expression. You can use
     * most of the Spring Security SpringEL expressions.
     *
     * @return Default endpoint security rule as SpringEL expression.
     */
    String getDefaultEndpointSecurityRule();

    /**
     * You may want to provide an own root object for SpringEL expression evaluation.
     * {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest} will normally
     * instantiate a {@link com.hedgehogsmind.springcouchrest.workers.springel.CouchRestSpelRoot} if you do
     * not provide an object here. You may also extend
     * {@link com.hedgehogsmind.springcouchrest.workers.springel.CouchRestSpelRoot}.
     *
     * @return Optional overwriting root object for SpringEL expression evaluation.
     */
    Optional<Object> getSpringElEvaluationRootObject();

}
