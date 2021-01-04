package com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertible;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.MappedEntityResource;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.MappedEntitySubHandlerBase;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles storing new entity instances (no path variables) and updating existing ones.
 * The body must be deserializable as JSON. In case an update attempt is requested
 * (by specifying the id as a path variable) the existing entity will be updated. Only
 * the field values will be updated which have been specified in the JSON body.
 */
public class MappedEntityPostHandler
        extends MappedEntitySubHandlerBase {

    /**
     * Stores parent entity resource.
     *
     * @param parentEntityResource Parent entity resource.
     */
    public MappedEntityPostHandler(MappedEntityResource parentEntityResource) {
        super(parentEntityResource);
    }

    /**
     * Checks if method is POST and if there are 0-1 path variables.
     *
     * @param request                    Request.
     * @param method                     HTTP method of request.
     * @param fullPath                   Full path of request with trailing slash.
     * @param pathAfterResource          Path after parent resource's path. Ends with trailing slash or is empty.
     * @param pathVariablesAfterResource PathAfterResource split into parts separated by slash. Empty trailing parts ignored.
     * @return True if method POST and 0-1 path vars.
     */
    @Override
    public boolean accepts(HttpServletRequest request,
                           String method,
                           String fullPath,
                           String pathAfterResource,
                           String[] pathVariablesAfterResource) {

        return method.equals("POST") &&
                pathVariablesAfterResource.length <= 1;
    }

    /**
     * <p>
     * Tries to serialize given body as entity instance. The instance will then be saved. The
     * saved entity is then returned.
     * </p>
     *
     * <p>
     * In case an id value has been provided using a path variable, this method tries to fetch the existing
     * entity and then uses the body for updating the values. By this, only updating a
     * subset of fields is supported.
     * </p>
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

        try {
            // We use an ObjectReader instead of the ObjectMapper, because we may need
            // to pass an existing entity instance for value updates
            ObjectReader objectReader = objectMapper.readerFor(getEntityClass());

            // in case we have an id, we try to parse it, check existence and store the id value in the instance
            if ( pathVariablesAfterResource.length == 1 ) {
                final Object parsedId = parseId(pathVariablesAfterResource[0]);

                final Optional<Object> existingEntity = getRepository().findById(parsedId);

                if ( existingEntity.isEmpty() ) {
                    return Couch2rProblems.NOT_FOUND.toResponseEntity();
                }

                // We pass the existing entity for updates to reader
                objectReader = objectReader.withValueToUpdate(existingEntity.get());
            }

            final String body = request.getReader().lines().collect(Collectors.joining("\n"));
            final Object serializedData = objectReader.readValue(body);

            // TODO @peter validate serializedData

            final Object savedEntity = getRepository().save(serializedData);
            return ResponseEntity.ok(savedEntity);

        } catch ( JsonProcessingException e ) {
            return Couch2rProblems.INVALID_DATA.toResponseEntity();

        } catch ( IOException e ) {
            // TODO @peter better response >> maybe own exception so that error will be logged?
            return Couch2rProblems.UNKNOWN_PROBLEM.toResponseEntity();
        }
    }
}
