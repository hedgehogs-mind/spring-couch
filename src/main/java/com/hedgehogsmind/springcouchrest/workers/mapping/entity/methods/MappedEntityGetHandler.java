package com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntitySubHandlerBase;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Handles GET requests for {@link MappedEntityResource}. Either delivers all entities or just
 * a single one if an id has been provided via a path variable.
 */
public class MappedEntityGetHandler
        extends MappedEntitySubHandlerBase {

    /**
     * Stores parent entity resource.
     *
     * @param parentEntityResource Parent entity resource.
     */
    public MappedEntityGetHandler(MappedEntityResource parentEntityResource) {
        super(parentEntityResource);
    }

    /**
     * Checks if method is GET and if there are either no path variables or only one.
     *
     * @param request                    Request.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @return True if GET and 0-1 path variables.
     */
    @Override
    public boolean accepts(HttpServletRequest request,
                           String method,
                           String fullPath,
                           String pathAfterResource,
                           String[] pathVariablesAfterResource) {

        return method.equals("GET") &&
                pathVariablesAfterResource.length <= 1;
    }

    @Override
    public ResponseEntity handle(HttpServletRequest request,
                                 ObjectMapper objectMapper,
                                 Locale locale,
                                 String method,
                                 String fullPath,
                                 String pathAfterResource,
                                 String[] pathVariablesAfterResource,
                                 Map<String, String[]> queryParameters) {

        if (pathVariablesAfterResource.length == 0) {

            // Simple get all
            return ResponseEntity.ok(
                    getRepository().findAll()
            );

        } else {

            // Get one by id
            final Object parsedId = parseId(pathVariablesAfterResource[0]);

            final Optional entityInstance = getRepository().findById(parsedId);

            return entityInstance.isPresent() ?
                    ResponseEntity.ok(entityInstance.get()) :
                    CouchRestProblems.NOT_FOUND.toResponseEntity();
        }
    }
}
