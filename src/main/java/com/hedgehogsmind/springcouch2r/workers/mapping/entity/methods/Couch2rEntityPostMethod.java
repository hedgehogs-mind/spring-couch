package com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.Couch2rEntityMapping;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.Couch2rEntityMethod;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles posting data.
 */
public class Couch2rEntityPostMethod extends Couch2rEntityMethod {

    public Couch2rEntityPostMethod(Couch2rEntityMapping entityMappingParent) {
        super(entityMappingParent);
    }

    @Override
    public Optional<ResponseEntity> handle(
            HttpServletRequest request,
            ObjectMapper objectMapper,
            Locale locale,
            String method,
            String[] pathVariables,
            Map<String, String[]> queryParameters
    ) {
        if ( method.equals("POST") &&
                pathVariables.length <= 1 &&
                request.getContentLengthLong() > 0 ) {

            try {
                final String body = request.getReader().lines().collect(Collectors.joining("\n"));
                final Object serializedData = objectMapper.readValue(body, getEntityClass());

                // in case we have an id, we try to parse it, check existence and store the id value in the instance
                if ( pathVariables.length == 1 ) {
                    final Object parsedId = parseId(pathVariables[0]);

                    if ( !getRepository().existsById(parsedId) ) {
                        return Optional.of(Couch2rProblems.NOT_FOUND.toResponseEntity());
                    }

                    setIdValue(serializedData, parsedId);
                }

                // TODO @peter validate serializedData

                final Object savedEntity = getRepository().save(serializedData);

                return Optional.of(ResponseEntity.ok(savedEntity));

            } catch ( JsonProcessingException e ) {
                // TODO @peter can we provide a better error message
                return Optional.of(Couch2rProblems.UNKNOWN_PROBLEM.toResponseEntity());
            } catch ( IOException e ) {
                // TODO @peter better response
                return Optional.of(Couch2rProblems.UNKNOWN_PROBLEM.toResponseEntity());
            } catch ( RuntimeException e ) {
                // TODO @peter log or own exception (peter from the future says: own runtime exception)
                return Optional.of(Couch2rProblems.UNKNOWN_PROBLEM.toResponseEntity());
            }

        }

        return Optional.empty();
    }
}
