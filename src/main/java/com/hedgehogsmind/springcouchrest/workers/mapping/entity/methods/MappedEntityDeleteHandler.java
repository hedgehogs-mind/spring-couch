package com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntitySubHandlerBase;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * Handles deleting one entity by id.
 */
public class MappedEntityDeleteHandler extends MappedEntitySubHandlerBase {

    /**
     * Stores parent resource.
     * @param parentEntityResource Entity resource which created this handler.
     */
    public MappedEntityDeleteHandler(MappedEntityResource parentEntityResource) {
        super(parentEntityResource);
    }

    /**
     * Accepts requests with HTTP method DELETE and one path variable (being the ID).
     * @param request                    Request.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @return True if method DELETE and one path variable is present.
     */
    @Override
    public boolean accepts(HttpServletRequest request,
                           String method,
                           String fullPath,
                           String pathAfterResource,
                           String[] pathVariablesAfterResource) {

        return method.equals("DELETE") && pathVariablesAfterResource.length == 1;
    }

    /**
     * First checks, that entity referenced by id exists. If so, it will be deleted.
     * 
     * @param request                    Request to handle.
     * @param objectMapper               ObjectMapper to use for JSON (de-) serialization.
     * @param locale                     Locale of request or a default one.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @param queryParameters            Query parameters.
     * @return
     */
    @Override
    public ResponseEntity handle(HttpServletRequest request,
                                 ObjectMapper objectMapper,
                                 Locale locale,
                                 String method,
                                 String fullPath,
                                 String pathAfterResource,
                                 String[] pathVariablesAfterResource,
                                 Map<String, String[]> queryParameters) {

        final Object parsedId = parseId(pathVariablesAfterResource[0]);

        if ( !getRepository().existsById(parsedId) ) return CouchRestProblems.NOT_FOUND.toResponseEntity();

        getRepository().deleteById(parsedId);

        return ResponseEntity.ok().build();
    }
}
