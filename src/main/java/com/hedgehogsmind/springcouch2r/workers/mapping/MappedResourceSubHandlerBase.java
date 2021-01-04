package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Base class for all resource sub handlers. This class introduces
 * the two methods {@link #accepts(HttpServletRequest, String, String, String, String[])}
 * and {@link #handle(HttpServletRequest, ObjectMapper, Locale, String, String, String, String[], Map)} -
 * extended versions of the ones defined in {@link MappingHandler}.
 * </p>
 *
 * <p>
 * This class implements the interface's methods. There data is prefetched and preprocessed
 * from the given request and passed to the extended methods. This simplifies the data access for
 * implementation. <b>The overall purpose of this class is convenience.</b>
 * </p>
 */
public abstract class MappedResourceSubHandlerBase
        implements MappingHandler {

    private final MappedResource parentResource;

    /**
     * Stores parent resource.
     *
     * @param parentResource Parent resource which created this sub handler.
     */
    public MappedResourceSubHandlerBase(MappedResource parentResource) {
        this.parentResource = parentResource;
    }

    /**
     * Obtains full path of given request. Convenience method.
     *
     * @param request Request.
     * @return Full path of request with trailing slash.
     */
    protected String getRequestsFullPath(final HttpServletRequest request) {
        return Couch2rRequestUtil.getRequestPathWithTrailingSlash(request);
    }

    /**
     * Removes basically parent resource's path from given fullPath.
     *
     * @param fullPath Full path to cut of parent resource's path from.
     * @return Path after parent resource path.
     */
    protected String getRequestsPathAfterParentResource(final String fullPath) {
        return fullPath.substring(parentResource.getResourcePathWithTrailingSlash().length());
    }

    /**
     * Splits path after resource into parts separated by slash. Convenience method.
     *
     * @param pathAfterParentResource Path after resource.
     * @return Path variables separated by slashes. Empty trailing parts are ignored.
     */
    protected String[] getRequestsPathVariablesAfterParentResource(
            final String pathAfterParentResource
    ) {
        // we do not need to cut of trailing slash, because split ignores trailing empty strings
        return (pathAfterParentResource.isEmpty() || pathAfterParentResource.equals("/")) ?
                new String[0] :
                pathAfterParentResource.split("/");
    }

    /**
     * <p>
     * Preprocesses and extracts important parts from request's data and passes it to
     * {@link #accepts(HttpServletRequest, String, String, String, String[])}.
     * Serves as an abstraction layer for the extending class/implementation.
     * </p>
     *
     * <p>
     * <b>Implementation must be able to assume,
     * that the path at least starts with the parent resource's path.</b>
     * </p>
     *
     * @param request Request to evaluate.
     * @return Result of {@link #accepts(HttpServletRequest, String, String, String, String[])}.
     */
    @Override
    public boolean accepts(HttpServletRequest request) {
        final String fullPath = getRequestsFullPath(request);
        final String pathAfterResource = getRequestsPathAfterParentResource(fullPath);
        final String[] pathVariables = getRequestsPathVariablesAfterParentResource(pathAfterResource);

        return accepts(
                request,
                request.getMethod(),
                fullPath,
                pathAfterResource,
                pathVariables
        );
    }

    /**
     * Preprocesses and extracts important parts from request's data and passes it to
     * {@link #handle(HttpServletRequest, ObjectMapper, Locale, String, String, String, String[], Map)}.
     * Serves as an abstraction layer for the extending class/implementation. Uses {@link Locale#ENGLISH}
     * as default request locale.
     *
     * @param request      Request to handle.
     * @param objectMapper ObjectMapper to use for JSON (de-) serialization.
     * @return
     */
    @Override
    public ResponseEntity handle(HttpServletRequest request, ObjectMapper objectMapper) {
        final String fullPath = getRequestsFullPath(request);
        final String pathAfterResource = getRequestsPathAfterParentResource(fullPath);
        final String[] pathVariables = getRequestsPathVariablesAfterParentResource(pathAfterResource);

        return handle(
                request,
                objectMapper,
                Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH),
                request.getMethod(),
                fullPath,
                pathAfterResource,
                pathVariables,
                request.getParameterMap()
        );
    }

    /**
     * <p>
     * Method the extending class needs to implement. In case this sub handler can handle the given request,
     * this method shall return true. If it is not capable of handling the request, it should return false.
     * </p>
     *
     * <p>
     * <b>Implementation must be able to assume,
     * that the path at least starts with the parent resource's path.</b>
     * </p>
     *
     * @param request                    Request.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @return True of this sub handler can handle the given request, otherwise false.
     */
    public abstract boolean accepts(
            HttpServletRequest request,
            String method,
            String fullPath,
            String pathAfterResource,
            String[] pathVariablesAfterResource
    );

    /**
     * Handles the given request. Implementation can assume, that all check made in
     * {@link #accepts(HttpServletRequest, String, String, String, String[])} still apply
     * to the given request.
     *
     * @param request                    Request to handle.
     * @param objectMapper               ObjectMapper to use for JSON (de-) serialization.
     * @param locale                     Locale of request or a default one.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @param queryParameters            Query parameters.
     * @return Result as ResponseEntity.
     */
    public abstract ResponseEntity handle(
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Locale locale,
            String method,
            String fullPath,
            String pathAfterResource,
            String[] pathVariablesAfterResource,
            Map<String, String[]> queryParameters
    );

    /**
     * Resource which created this sub handler.
     *
     * @return Parent resource.
     */
    protected MappedResource getParentResource() {
        return parentResource;
    }

}
