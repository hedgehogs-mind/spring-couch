package com.hedgehogsmind.springcouch2r.workers.mapping.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMappedResourceBase;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMappedResourceWithMethodsBase;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rResourceMethod;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods.Couch2rEntityGetMethod;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import javax.persistence.metamodel.EntityType;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 *     Path is stored with trailing slash so that it is prefix free.
 *     <br>
 *     Problem example if we would not use trailing slashes:
 *     <ul>
 *         <li>Mapping 1: <code>/api/code</code></li>
 *         <li>Mapping 2: <code>/api/codeRedeemed</code></li>
 *         <li>Incoming request: <code>/api/code</code> (here startsWith check would not lead to unique result!</li>
 *     </ul>
 * </p>
 */
public class Couch2rEntityMapping extends Couch2rMappedResourceWithMethodsBase {

    private final CrudRepository repository;

    private final EntityType entityType;

    /**
     * Stores given values.
     *
     * @param fullPathWithTrailingSlash Full path including Couch2r base path.
     * @param couch2rResourcePathWithTrailingSlash Path after Couch2r base path.
     * @param discoveredUnit Source of this mapping.
     * @param repository Repository of the entity.
     * @param entityType Handled entity.
     */
    public Couch2rEntityMapping(
            String fullPathWithTrailingSlash,
            String couch2rResourcePathWithTrailingSlash,
            Couch2rDiscoveredUnit discoveredUnit,
            CrudRepository repository,
            EntityType entityType
    ) {
        super(fullPathWithTrailingSlash, couch2rResourcePathWithTrailingSlash, discoveredUnit);

        this.repository = repository;
        this.entityType = entityType;
    }

    @Override
    protected List<Couch2rResourceMethod> createResourceMethods() {
        return List.of(
                new Couch2rEntityGetMethod(this)
        );
    }

    public CrudRepository getRepository() {
        return repository;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
