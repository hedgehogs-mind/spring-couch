package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * This class resembles a resource which is mapped by Couch2r. It lays within the
 * {@link Couch2rConfiguration#getCouch2rBasePath()} path.
 */
public interface Couch2rMappedResource {

    /**
     * Delivers the full path.
     *
     * @return
     */
    String getFullPath();

    /**
     * Delivers the entry resource name of the group.
     *
     * @return Resource name. Shall end with trailing slash.
     */
    String getCouch2rResourcePath();

    /**
     * Returns the source of this mapping group.
     * @return Source.
     */
    Couch2rDiscoveredUnit getMappingSource();

    ResponseEntity handle(HttpServletRequest request, ObjectMapper objectMapper);

}
