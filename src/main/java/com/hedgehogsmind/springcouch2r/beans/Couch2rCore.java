package com.hedgehogsmind.springcouch2r.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rEntityAlreadyManagedByRepositoryException;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rNoConfigurationFoundException;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rNoUniqueConfigurationFoundException;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rResourcePathClashException;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.configuration.ValidatedAndNormalizedCouch2rConfiguration;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredCrudRepository;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.util.Couch2rEntityUtil;
import com.hedgehogsmind.springcouch2r.util.Couch2rPathUtil;
import com.hedgehogsmind.springcouch2r.workers.discovery.Couch2rDiscovery;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMapping;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Couch2rCore {

    private final ApplicationContext applicationContext;

    private final EntityManager entityManager;

    private final Optional<ObjectMapper> globalObjectMapper;

    private ObjectMapper couch2rObjectMapper;

    private Couch2rConfiguration couch2rConfiguration;

    private Couch2rDiscovery couch2rDiscovery;

    private Set<Couch2rMapping> couch2rMappings;

    /**
     * Dependency injection constructor.
     *
     * @param applicationContext ApplicationContext. Used to find beans.
     * @param entityManager EntityManager. Used to fetch managed entities.
     * @param globalObjectMapper ObjectMapper globally configured (optional). Maybe used for JSON mapping.
     */
    public Couch2rCore(ApplicationContext applicationContext, EntityManager entityManager, Optional<ObjectMapper> globalObjectMapper) {
        this.applicationContext = applicationContext;
        this.entityManager = entityManager;
        this.globalObjectMapper = globalObjectMapper;
    }

    /**
     * <p>
     *     First fetches {@link Couch2rConfiguration} and then applies settings via
     *     {@link #applyCouch2rConfiguration()}.
     * </p>
     */
    @PostConstruct
    public void setup() {
        fetchCouch2rConfiguration();
        applyCouch2rConfiguration();

        this.couch2rDiscovery = new Couch2rDiscovery(applicationContext, entityManager);

        setupMappings();
    }

    /**
     * Searches a bean implementing {@link Couch2rConfiguration}, validates and normalizes it via
     * {@link ValidatedAndNormalizedCouch2rConfiguration}.
     *
     * @throws Couch2rNoUniqueConfigurationFoundException if no unique {@link Couch2rConfiguration} bean exists.
     * @throws Couch2rNoConfigurationFoundException if no {@link Couch2rConfiguration} bean exists.
     */
    protected void fetchCouch2rConfiguration() {
        try {
            final Couch2rConfiguration bean = applicationContext.getBean(Couch2rConfiguration.class);
            this.couch2rConfiguration = new ValidatedAndNormalizedCouch2rConfiguration(bean);

        } catch ( NoUniqueBeanDefinitionException e ) {
            throw new Couch2rNoUniqueConfigurationFoundException("No unique Couch2rConfigurations found.");
        } catch ( NoSuchBeanDefinitionException e ) {
            throw new Couch2rNoConfigurationFoundException("No Couch2rConfiguration found.");
        }
    }

    /**
     * Does the following:
     * <ul>
     *     <li>{@link #setupObjectMapper()}</li>
     * </ul>
     */
    protected void applyCouch2rConfiguration() {
        setupObjectMapper();
    }

    /**
     * Sets object mapper either to one specified in {@link Couch2rConfiguration}, to global one, or creates a new one.
     */
    protected void setupObjectMapper() {
        if ( couch2rConfiguration.getCouch2rObjectMapper().isPresent() ) {
            this.couch2rObjectMapper = couch2rConfiguration.getCouch2rObjectMapper().get();
        } else if ( globalObjectMapper.isPresent() ) {
            this.couch2rObjectMapper = globalObjectMapper.get();
        } else {
            this.couch2rObjectMapper = new ObjectMapper();
        }
    }

    /**
     * Creates new mapping set and calls {@link #setupRepositoryMappings()} and
     * {@link #setupEntityMappings()}.
     */
    protected void setupMappings() {
        couch2rMappings = new HashSet<>();

        setupRepositoryMappings();
        setupEntityMappings();
    }

    /**
     * <p>
     *      Creates new Couch2rMapping for each discovered repo. Resource path is constructed
     *      by {@link #constructResourcePathAndAssertNoPathClash(Couch2rDiscoveredUnit)}.
     *      Registers mapping at internal set.
     * </p>
     */
    protected void setupRepositoryMappings() {
        couch2rDiscovery.getDiscoveredCrudRepositories().forEach(discoveredRepo -> {
            final Couch2rMapping newRepositoryMapping = new Couch2rMapping(
                    discoveredRepo,
                    constructResourcePathAndAssertNoPathClash(discoveredRepo),
                    discoveredRepo.getCrudRepository(),
                    discoveredRepo.getEntityType()
            );

            couch2rMappings.add(newRepositoryMapping);
        });
    }

    /**
     * <p>
     *     For each discovered entity: Checks that there is no repository which already handles
     *     the entity type.
     * </p>
     *
     * <p>
     *     Then a new mapping is created. Resource path is constructed using
     *     {@link #constructResourcePathAndAssertNoPathClash(Couch2rDiscoveredUnit)}.
     *     Then mapping is registered at internal set.
     * </p>
     */
    protected void setupEntityMappings() {
        couch2rDiscovery.getDiscoveredEntities().forEach(discoveredEntity -> {

            // First check if entity is already managed by a repo
            final Optional<Couch2rDiscoveredCrudRepository> discoveredRepo =
                    couch2rDiscovery.getDiscoveredCrudRepositories().stream()
                        .filter(dr -> dr.getEntityClass() == discoveredEntity.getEntityClass())
                        .findAny();

            if ( discoveredRepo.isPresent() ) {
                throw new Couch2rEntityAlreadyManagedByRepositoryException(
                        "Entity '"+discoveredEntity.getEntityClass()+"' has been tagged with @Couch2r but is" +
                                " already managed by a repository which is also tagged with @Couch2r.\n" +
                                "Repository class: "+discoveredRepo.get().getTagAnnotationSource()+"\n" +
                                "Remove the annotation on the repository or on the entity."
                );
            }

            final Couch2rMapping newEntityMapping = new Couch2rMapping(
                    discoveredEntity,
                    constructResourcePathAndAssertNoPathClash(discoveredEntity),
                    new SimpleJpaRepository(
                            discoveredEntity.getEntityClass(),
                            entityManager
                    ),
                    discoveredEntity.getEntityType()
            );

            couch2rMappings.add(newEntityMapping);
        });
    }

    /**
     * <p>
     *     <ul>
     *         <li>Computes resource name: Entity class name or name() of Couch2r annotation if not empty.</li>
     *         <li>Compute path using {@link Couch2rConfiguration#getCouch2rBasePath()}</li>
     *         <li>Check if there is already a mapping with the same path</li>
     *         <li>Return path (with trailing slash)</li>
     *     </ul>
     * </p>
     *
     * @param discoveredUnit Discovered unit to create resource path for (based on couch2r config).
     * @return Resource path with trailing slash.
     * @throws Couch2rResourcePathClashException if there is already a mapping with the same path.
     */
    protected String constructResourcePathAndAssertNoPathClash(final Couch2rDiscoveredUnit discoveredUnit) {
        final String resourceName = !discoveredUnit.getTagAnnotation().resourceName().isBlank() ?
                discoveredUnit.getTagAnnotation().resourceName() :
                Couch2rEntityUtil.getEntityClassNameSnakeCase(discoveredUnit.getEntityClass());

        final String path = Couch2rPathUtil.normalizeWithTrailingSlash(
                couch2rConfiguration.getCouch2rBasePath() +
                        resourceName
        );

        final Optional<Couch2rMapping> existingMapping =
                getMappingByPath(path);

        if ( existingMapping.isPresent() ) {
            throw new Couch2rResourcePathClashException(
                    "There is a path clash for entity '"+discoveredUnit.getEntityClass()+"'. The path '"+path+"' is already taken.\n" +
                            "Existing mapping's source: "+existingMapping.get().getDiscoveredUnit().getTagAnnotationSource()+"\n" +
                            "New (attempted) mapping's source: "+discoveredUnit.getTagAnnotationSource()+"\n" +
                            "You may change the entity class name or specify different names in the Couch2r annotation using 'resourceName'."
            );
        }

        return path;
    }

    /**
     * Tries to find a Couch2rMapping by path.
     * @param path Path to search for.
     * @return Mapping or empty.
     */
    protected Optional<Couch2rMapping> getMappingByPath(final String path) {
        if ( !path.endsWith("/") ) throw new IllegalArgumentException("path must end with trailing slash");

        return couch2rMappings.stream()
                .filter(m -> m.getPathWithTrailingSlash().equals(path))
                .findAny();
    }

    public ObjectMapper getCouch2rObjectMapper() {
        return couch2rObjectMapper;
    }

    public Couch2rConfiguration getCouch2rConfiguration() {
        return couch2rConfiguration;
    }

    public Set<Couch2rMapping> getCouch2rMappings() {
        return couch2rMappings;
    }

    public void setCouch2rMappings(Set<Couch2rMapping> couch2rMappings) {
        this.couch2rMappings = couch2rMappings;
    }
}
