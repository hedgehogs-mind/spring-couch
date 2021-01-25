package com.hedgehogsmind.springcouchrest.workers.mapping.entity;

import com.hedgehogsmind.springcouchrest.annotations.crud.CrudMethods;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappingHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityDeleteHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityGetHandler;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.methods.MappedEntityPostHandler;
import com.hedgehogsmind.springcouchrest.workers.security.ResourceCrudSecurityHandler;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles request for a mapped entity.
 */
public class MappedEntityResource
        extends MappedResource {

    private final EntityType entityType;

    private final CrudRepository repository;

    private final Optional<CrudMethods> methodsToggle;

    private final ResourceCrudSecurityHandler securityHandler;

    /**
     * Stores given values and calls super constructor, which initializes sub handler mappings.
     * Furthermore, this constructor initializes a {@link ResourceCrudSecurityHandler} and tries to
     * fetch an optional {@link CrudMethods} annotation for method toggling.
     *
     * @param core                          CouchRest core instance which created this entity resource.
     * @param discoveredUnit                Source of this mapping.
     * @param resourcePathWithTrailingSlash Resource path with trailing slash.
     * @param entityType                    EntityType of mapped entity.
     * @param repository                    Repository for mapped entity.
     */
    public MappedEntityResource(CouchRestCore core,
                                DiscoveredUnit discoveredUnit,
                                String resourcePathWithTrailingSlash,
                                EntityType entityType,
                                CrudRepository repository) {

        super(core, discoveredUnit, resourcePathWithTrailingSlash);

        if (entityType == null) throw new IllegalArgumentException("entityType must not be null");
        if (repository == null) throw new IllegalArgumentException("repository must not be null");

        this.entityType = entityType;
        this.repository = repository;
        this.securityHandler = new ResourceCrudSecurityHandler(this);
        this.methodsToggle = discoveredUnit.getOptionalCouchRestModifierAnnotation(CrudMethods.class);
    }

    /**
     * Conditionally creates CRUD handlers. In case, {@link #getMethodsToggle()} is empty,
     * all CRUD handlers will be added. If present, only those for which the flag of the annotation is true.
     *
     * @return List of entity resource handlers.
     */
    @Override
    protected List<MappingHandler> createSubMappingHandlers() {
        final ArrayList<MappingHandler> handlers = new ArrayList<>();

        if ( methodsToggle.isEmpty() || methodsToggle.get().get() ) {
            handlers.add(new MappedEntityGetHandler(this));
        }

        if ( methodsToggle.isEmpty() || methodsToggle.get().saveUpdate() ) {
            handlers.add(new MappedEntityPostHandler(this));
        }

        if ( methodsToggle.isEmpty() || methodsToggle.get().delete() ) {
            handlers.add(new MappedEntityDeleteHandler(this));
        }

        return handlers;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public CrudRepository getRepository() {
        return repository;
    }

    public ResourceCrudSecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    public Optional<CrudMethods> getMethodsToggle() {
        return methodsToggle;
    }
}
