package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 *     This class is an extension of {@link Couch2rMappedResourceBase} and
 *     implements a priority based queue of {@link Couch2rResourceMethod}s.
 * </p>
 *
 * <p>
 *     An extending class only needs to configure and add {@link Couch2rResourceMethod}s
 *     in the correct order. It there only needs to implement {@link #createResourceMethods()}.
 * </p>
 *
 */
public abstract class Couch2rMappedResourceWithMethodsBase extends Couch2rMappedResourceBase {

    private final List<Couch2rResourceMethod> resourceMethods;

    /**
     * Stores paths and initializes resource methods via {@link #createResourceMethods()}.
     *
     * @param fullPathWithTrailingSlash Full path of this resource.
     * @param couch2rResourcePathWithTrailingSlash Couch2r resource path of this resource.
     * @param mappingSource Source of this mapping.
     */
    public Couch2rMappedResourceWithMethodsBase(
            String fullPathWithTrailingSlash,
            String couch2rResourcePathWithTrailingSlash,
            Couch2rDiscoveredUnit mappingSource
    ) {
        super(fullPathWithTrailingSlash, couch2rResourcePathWithTrailingSlash, mappingSource);
        this.resourceMethods = createResourceMethods();
    }

    /**
     * This method shall be implemented by the extending class. It shall
     * return a list of {@link Couch2rResourceMethod} in the correct order of execution
     * respectively priority.
     *
     * @return List of resource methods to use for request handling.
     */
    protected abstract List<Couch2rResourceMethod> createResourceMethods();

    /**
     * <p>
     *     One by one calls {@link Couch2rResourceMethod#handle(HttpServletRequest, ObjectMapper, Locale, String, String[], Map)}
     *     of all {@link #getResourceMethods()}. In case one returns real ResponseEntity result, it will be returned
     *     and further steps will be cancelled.
     * </p>
     *
     * <p>
     *     In case no {@link Couch2rResourceMethod} handled the request, {@link Couch2rProblems#NOT_FOUND} will be returned
     *     in order to tell the client, that no mapping exists.
     * </p>
     *
     * @param request Request.
     * @param objectMapper ObjectMapper for JSON actions.
     * @param locale Locale of request.
     * @param pathVariables Path variables after {@link #getFullPath()}.
     * @param queryParameters Query parameters.
     * @return
     */
    @Override
    public ResponseEntity handle(
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Locale locale,
            String method,
            String[] pathVariables,
            Map<String, String[]> queryParameters
    ) {
        for ( final Couch2rResourceMethod resourceMethod : resourceMethods ) {
            final Optional<ResponseEntity> result = resourceMethod.handle(
                    request,
                    objectMapper,
                    locale,
                    method,
                    pathVariables,
                    queryParameters
            );

            if ( result.isPresent() ) return result.get();
        }

        return Couch2rProblems.NOT_FOUND.toResponseEntity();
    }

    protected List<Couch2rResourceMethod> getResourceMethods() {
        return this.resourceMethods;
    }

}
