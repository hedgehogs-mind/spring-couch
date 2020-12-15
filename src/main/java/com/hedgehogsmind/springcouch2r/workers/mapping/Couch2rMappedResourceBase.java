package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 *     Base implementation of {@link Couch2rMappedResource}. This eliminates storing the paths and source over and
 *     over again in each implementing class.
 * </p>
 *
 * <p>
 *     This class also delivers some convenience for implementing the handler method by extracting path variables,
 *     query parameters and providing id parsing methods etc.
 * </p>
 */
public abstract class Couch2rMappedResourceBase implements Couch2rMappedResource {

    private final String fullPathWithTrailingSlash;

    private final String couch2rResourcePathWithTrailingSlash;

    private final Couch2rDiscoveredUnit mappingSource;

    /**
     * Stores values and asserts that both paths end with a trailing slash.
     *
     * @param fullPathWithTrailingSlash Full path including Couch2r base path.
     * @param couch2rResourcePathWithTrailingSlash Path after Couch2r base path.
     * @param mappingSource The source/cause for this mapped resource to be created.
     */
    public Couch2rMappedResourceBase(String fullPathWithTrailingSlash, String couch2rResourcePathWithTrailingSlash, Couch2rDiscoveredUnit mappingSource) {
        if ( !fullPathWithTrailingSlash.endsWith("/") ) {
            throw new IllegalArgumentException("fullPathWithTrailingSlash does not end with trailing slash. Was: "
                    + fullPathWithTrailingSlash);
        }

        if ( !couch2rResourcePathWithTrailingSlash.endsWith("/") ) {
            throw new IllegalArgumentException("couch2rResourcePathWithTrailingSlash does not end with trailing slash. Was: "
                    + couch2rResourcePathWithTrailingSlash);
        }

        this.fullPathWithTrailingSlash = fullPathWithTrailingSlash;
        this.couch2rResourcePathWithTrailingSlash = couch2rResourcePathWithTrailingSlash;
        this.mappingSource = mappingSource;
    }

    @Override
    public String getFullPath() {
        return fullPathWithTrailingSlash;
    }

    @Override
    public String getCouch2rResourcePath() {
        return couch2rResourcePathWithTrailingSlash;
    }

    @Override
    public Couch2rDiscoveredUnit getMappingSource() {
        return mappingSource;
    }

    /**
     * Fetches path, computes additional path variables, fetches locale etc and finally calls
     * {@link #handle(HttpServletRequest, ObjectMapper, Locale, String, String[], Map)}.
     *
     * This method makes implementing the handler method way easier, because necessary data
     * is fetched beforehand (e.g. path variables).
     *
     * @param request Request.
     * @param objectMapper ObjectMapper for JSON actions.
     * @return Response of {@link #handle(HttpServletRequest, ObjectMapper, Locale, String, String[], Map)} implementation.
     */
    @Override
    public ResponseEntity handle(HttpServletRequest request, ObjectMapper objectMapper) {
        final String path = Couch2rRequestUtil.getRequestPathWithTrailingSlash(request);
        final String pathWithinCouch2rResource = path.substring(getFullPath().length()).trim();

        final String[] additionalPathVariables = pathWithinCouch2rResource.isEmpty() ?
                new String[0] :
                pathWithinCouch2rResource.split("/");

        return handle(
                request,
                objectMapper,
                Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH),
                request.getMethod(),
                additionalPathVariables,
                request.getParameterMap()
        );
    }

    /**
     * Handles request.
     *
     * @param request Request.
     * @param objectMapper ObjectMapper for JSON actions.
     * @param locale Locale of request.
     * @param pathVariables Path variables after {@link #getFullPath()}.
     * @param queryParameters Query parameters.
     * @return Response.
     */
    public abstract ResponseEntity handle(
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Locale locale,
            String method,
            String[] pathVariables,
            Map<String, String[]> queryParameters
    );

}
