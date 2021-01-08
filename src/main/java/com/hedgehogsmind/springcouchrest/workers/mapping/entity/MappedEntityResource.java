package com.hedgehogsmind.springcouchrest.workers.mapping.entity;

import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappingHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityDeleteHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityGetHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityPostHandler;
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
    public MappedEntityResource(DiscoveredUnit discoveredUnit,
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
