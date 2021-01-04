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
import com.hedgehogsmind.springcouch2r.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.MappedEntityResource;
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

    private Set<MappedResource> mappedResources;

    /**
     * Dependency injection constructor.
     *
     * @param applicationContext ApplicationContext. Used to find beans.
     * @param entityManager      EntityManager. Used to fetch managed entities.
     * @param globalObjectMapper ObjectMapper globally configured (optional). Maybe used for JSON mapping.
     */
    public Couch2rCore(ApplicationContext applicationContext,
                       EntityManager entityManager,
                       Optional<ObjectMapper> globalObjectMapper) {
        this.applicationContext = applicationContext;
        this.entityManager = entityManager;
        this.globalObjectMapper = globalObjectMapper;
    }

    /**
     * <p>
     * First fetches {@link Couch2rConfiguration} and then applies settings via
     * {@link #applyCouch2rConfiguration()}.
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
     * @throws Couch2rNoConfigurationFoundException       if no {@link Couch2rConfiguration} bean exists.
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
        mappedResources = new HashSet<>();

        setupRepositoryMappings();
        setupEntityMappings();
    }

    /**
     * <p>
     * Creates new Couch2rMapping for each discovered repo. Resource path is constructed
     * by {@link #constructEntityResourcePathAndAssertNoPathClash(Couch2rDiscoveredUnit)}.
     * Registers mapping at internal set.
     * </p>
     */
    protected void setupRepositoryMappings() {
        couch2rDiscovery.getDiscoveredCrudRepositories().forEach(discoveredRepo -> {
            final MappedEntityResource newRepositoryMapping = new MappedEntityResource(
                    discoveredRepo,
                    constructFullEntityResourcePathAndAssertNoPathClash(discoveredRepo),
                    discoveredRepo.getEntityType(),
                    discoveredRepo.getCrudRepository()
            );

            mappedResources.add(newRepositoryMapping);
        });
    }

    /**
     * <p>
     * For each discovered entity: Checks that there is no repository which already handles
     * the entity type.
     * </p>
     *
     * <p>
     * Then a new mapping is created. Resource path is constructed using
     * {@link #constructEntityResourcePathAndAssertNoPathClash(Couch2rDiscoveredUnit)}.
     * Then mapping is registered at internal set.
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
                        "Entity '" + discoveredEntity.getEntityClass() + "' has been tagged with @Couch2r but is" +
                                " already managed by a repository which is also tagged with @Couch2r.\n" +
                                "Repository class: " + discoveredRepo.get().getTagAnnotationSource() + "\n" +
                                "Remove the annotation on the repository or on the entity."
                );
            }

            // We need to create a new repository instance for the entity and
            // apply bean processing > important for transactional proxies
            final SimpleJpaRepository rawRepo = new SimpleJpaRepository(
                    discoveredEntity.getEntityClass(),
                    entityManager
            );
            final String newBeanName = "Couch2r-Entity-Repo-" + discoveredEntity.getEntityType().getName();
            final SimpleJpaRepository finishedRepoBean = (SimpleJpaRepository) applicationContext
                    .getAutowireCapableBeanFactory()
                    .initializeBean(rawRepo, newBeanName);

            final MappedEntityResource newEntityMapping = new MappedEntityResource(
                    discoveredEntity,
                    constructFullEntityResourcePathAndAssertNoPathClash(discoveredEntity),
                    discoveredEntity.getEntityType(),
                    finishedRepoBean
            );

            mappedResources.add(newEntityMapping);
        });
    }

    /**
     * Convenience method. Prepends {@link Couch2rConfiguration#getCouch2rBasePath()} before
     *
     * @param discoveredUnit Discovered unit to create resource path for.
     * @return Couch2r base path plus resource path with trailing slash.
     * @link #constructEntityResourcePathAndAssertNoPathClash(Couch2rDiscoveredUnit)}.
     */
    protected String constructFullEntityResourcePathAndAssertNoPathClash(final Couch2rDiscoveredUnit discoveredUnit) {
        final String resourceName = !discoveredUnit.getTagAnnotation().resourceName().isBlank() ?
                discoveredUnit.getTagAnnotation().resourceName() :
                Couch2rEntityUtil.getEntityClassNameSnakeCase(discoveredUnit.getEntityClass());

        final String path = couch2rConfiguration.getCouch2rBasePath() + Couch2rPathUtil.normalizeWithTrailingSlash(resourceName);

        final Optional<MappedResource> existingMapping =
                getMappingByCouch2rResourcePath(path);

        if ( existingMapping.isPresent() ) {
            throw new Couch2rResourcePathClashException(
                    "There is a path clash for entity '" + discoveredUnit.getEntityClass() + "'. The path '" + path + "' is already taken.\n" +
                            "Existing mapping's source: " + existingMapping.get().getMappingSource().getTagAnnotationSource() + "\n" +
                            "New (attempted) mapping's source: " + discoveredUnit.getTagAnnotationSource() + "\n" +
                            "You may change the entity class name or specify different names in the Couch2r annotation using 'resourceName'."
            );
        }

        return path;
    }

    /**
     * Tries to find a Couch2rMapping by path ({@link MappedResource#getResourcePathWithTrailingSlash()}.
     *
     * @param path Path to search for.
     * @return Mapping or empty.
     */
    protected Optional<MappedResource> getMappingByCouch2rResourcePath(final String path) {
        if ( !path.endsWith("/") ) throw new IllegalArgumentException("path must end with trailing slash");

        return mappedResources.stream()
                .filter(m -> m.getResourcePathWithTrailingSlash().equals(path))
                .findAny();
    }

    public ObjectMapper getCouch2rObjectMapper() {
        return couch2rObjectMapper;
    }

    public Couch2rConfiguration getCouch2rConfiguration() {
        return couch2rConfiguration;
    }

    public Set<MappedResource> getMappedResources() {
        return mappedResources;
    }

}
