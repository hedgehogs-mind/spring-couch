package com.hedgehogsmind.springcouchrest.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.util.RequestUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This class bundles multiple {@link MappingHandler}s with the same path prefix.
 * This optimizes handler search by a lot, because the implementation of {@link #accepts(HttpServletRequest)}
 * first checks if the prefix matches. This prevents asking every single sub handler if it accepts the request.
 * </p>
 *
 * <p>
 * There is further optimization potential: The caller can directly call {@link #findHandler(HttpServletRequest)}
 * and return the result handler (if present) as the final {@link MappingHandler} instead of this one.
 * This prevents a second sub handler search in this {@link #handle(HttpServletRequest, ObjectMapper)}'s
 * implementation. The caller can directly call the handle method of the handler returned by
 * {@link #findHandler(HttpServletRequest)}.
 * </p>
 */
public abstract class MappedResource
        implements MappingHandler {

    private final CouchRestCore core;

    private final DiscoveredUnit discoveredUnit;

    private final String resourcePathWithTrailingSlash;

    private final List<MappingHandler> subMappingHandlers;

    /**
     * Stores path and initializes sub mapping handlers by calling {@link #createSubMappingHandlers()}.
     *
     * @param core                          CouchRest core instance which created this resource.
     * @param discoveredUnit                Source for the mapping.
     * @param resourcePathWithTrailingSlash Path of resource with trailing slash.
     */
    public MappedResource(CouchRestCore core, DiscoveredUnit discoveredUnit, String resourcePathWithTrailingSlash) {
        if ( core == null ) {
            throw new IllegalArgumentException("core must not be null.");
        }

        if (discoveredUnit == null) {
            throw new IllegalArgumentException("discoveredUnit must not be null.");
        }

        if (!resourcePathWithTrailingSlash.endsWith("/")) {
            throw new IllegalArgumentException("path must end with trailing slash.");
        }

        this.core = core;
        this.discoveredUnit = discoveredUnit;
        this.resourcePathWithTrailingSlash = resourcePathWithTrailingSlash;
        this.subMappingHandlers = createSubMappingHandlers();

        if (this.subMappingHandlers == null) {
            throw new IllegalStateException("Implementation of createSubMappingHandlers() returned null. " +
                    "Must return an instance of List.");
        }
    }

    /**
     * The implementation shall all mappings that shall be used for {@link #accepts(HttpServletRequest)}
     * and {@link #handle(HttpServletRequest, ObjectMapper)}.
     *
     * @return List of mapping handlers to use.
     */
    protected abstract List<MappingHandler> createSubMappingHandlers();

    /**
     * Checks if {@link #findHandler(HttpServletRequest)} delivers a sub handler.
     *
     * @param request Request to evaluate.
     * @return True of there is a sub handler in this resource.
     */
    @Override
    public boolean accepts(HttpServletRequest request) {
        return findHandler(request).isPresent();
    }

    /**
     * Tries to find a sub handler, which can handle the given request.
     * This method will not return a handler if the path of the request does not
     * start with this resource's path.
     *
     * @param request Request to find sub handler for.
     * @return Handler or empty.
     */
    public Optional<MappingHandler> findHandler(HttpServletRequest request) {
        final String path = RequestUtil.getRequestPathWithTrailingSlash(request);

        if (path.startsWith(resourcePathWithTrailingSlash)) {
            for (final MappingHandler subHandler : subMappingHandlers) {
                if (subHandler.accepts(request)) return Optional.of(subHandler);
            }
        }

        return Optional.empty();
    }

    /**
     * Calls handler of request. In case you pass a request, which has not been passed to {@link #accepts(HttpServletRequest)}
     * or did not result in true (for accepts), then this method will throw an {@link IllegalStateException}.
     *
     * @param request      Request to handle.
     * @param objectMapper ObjectMapper to use for JSON (de-) serialization.
     * @return Result of sub handler.
     * @throws IllegalAccessException if there is no sub handler for the given request.
     */
    @Override
    public ResponseEntity handle(HttpServletRequest request, ObjectMapper objectMapper) {
        final Optional<MappingHandler> handler = findHandler(request);

        if (handler.isEmpty()) {
            throw new IllegalStateException("MappedResource shall handle request, but can't find appropriate sub handler.");
        }

        return handler.get().handle(request, objectMapper);
    }

    /**
     * Source of this mapping.
     *
     * @return Source of mapping.
     */
    public DiscoveredUnit getMappingSource() {
        return discoveredUnit;
    }

    /**
     * Delivers path under which this resource is available.
     *
     * @return Path of this resource with trailing slash.
     */
    public String getResourcePathWithTrailingSlash() {
        return resourcePathWithTrailingSlash;
    }

    /**
     * Delivers sub handlers of this resource.
     *
     * @return List of sub handlers. An unmodifiable version.
     */
    protected List<MappingHandler> getSubMappingHandlers() {
        return Collections.unmodifiableList(subMappingHandlers);
    }

    /**
     * Returns core which created this resource.
     *
     * @return Related core.
     */
    public CouchRestCore getCore() {
        return core;
    }

}
