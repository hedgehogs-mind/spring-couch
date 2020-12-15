package com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.Couch2rEntityMapping;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.Couch2rEntityMethod;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 *     Handles all GET calls for entity itself in terms of CRUD operations.
 *     Currently supports:
 *     <ul>
 *         <li>Get all</li>
 *         <li>Get one by id</li>
 *     </ul>
 * </p>
 */
public class Couch2rEntityGetMethod extends Couch2rEntityMethod {

    /**
     * See {@link Couch2rEntityMethod}.
     * @param entityMappingParent Parent of this method.
     */
    public Couch2rEntityGetMethod(Couch2rEntityMapping entityMappingParent) {
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

        if ( method.equals("GET") ) {
            if ( pathVariables.length == 0 ) {

                // Simple get all
                return Optional.of(
                        ResponseEntity.ok(
                                getRepository().findAll()
                        )
                );

            } else if ( pathVariables.length == 1 ) {

                // Get one by id
                final Object parsedId = parseId(pathVariables[0]);

                final Optional<Object> entityInstance = getRepository().findById(parsedId);

                return Optional.of(
                        entityInstance.isPresent() ?
                                ResponseEntity.ok(entityInstance.get()) :
                                Couch2rProblems.NOT_FOUND.toResponseEntity()
                );
            }
        }

        return Optional.empty();
    }
}
