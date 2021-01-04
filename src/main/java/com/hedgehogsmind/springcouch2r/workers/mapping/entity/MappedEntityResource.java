package com.hedgehogsmind.springcouch2r.workers.mapping.entity;

import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouch2r.workers.mapping.MappingHandler;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods.MappedEntityDeleteHandler;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods.MappedEntityGetHandler;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.methods.MappedEntityPostHandler;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.EntityType;
import java.util.List;

/**
 * Handles request for a mapped entity.
 */
public class MappedEntityResource
        extends MappedResource {

    private final EntityType entityType;

    private final CrudRepository repository;

    /**
     * Stores given values and calls super constructor, which initializes sub handler mappings.
     *
     * @param discoveredUnit Source of this mapping.
     * @param resourcePathWithTrailingSlash Resource path with trailing slash.
     * @param entityType EntityType of mapped entity.
     * @param repository Repository for mapped entity.
     */
    public MappedEntityResource(Couch2rDiscoveredUnit discoveredUnit,
                                String resourcePathWithTrailingSlash,
                                EntityType entityType,
                                CrudRepository repository) {

        super(discoveredUnit, resourcePathWithTrailingSlash);

        if ( entityType == null ) throw new IllegalArgumentException("entityType must not be null");
        if ( repository == null ) throw new IllegalArgumentException("repository must not be null");

        this.entityType = entityType;
        this.repository = repository;
    }

    @Override
    protected List<MappingHandler> createSubMappingHandlers() {
        return List.of(
                new MappedEntityGetHandler(this),
                new MappedEntityPostHandler(this),
                new MappedEntityDeleteHandler(this)
        );
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public CrudRepository getRepository() {
        return repository;
    }
}
