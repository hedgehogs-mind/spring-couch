package com.hedgehogsmind.springcouchrest.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.beans.exceptions.EntityAlreadyManagedByRepositoryException;
import com.hedgehogsmind.springcouchrest.beans.exceptions.NoConfigurationFoundException;
import com.hedgehogsmind.springcouchrest.beans.exceptions.NoUniqueConfigurationFoundException;
import com.hedgehogsmind.springcouchrest.beans.exceptions.ResourcePathClashException;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.ValidatedAndNormalizedCouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredCrudRepository;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.util.EntityUtil;
import com.hedgehogsmind.springcouchrest.util.PathUtil;
import com.hedgehogsmind.springcouchrest.workers.discovery.CouchRestDiscovery;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CouchRestCore {

    private final ApplicationContext applicationContext;

    private final EntityManager entityManager;

    private final Optional<ObjectMapper> globalObjectMapper;

    private ObjectMapper couchRestObjectMapper;

    private CouchRestConfiguration couchRestConfiguration;

    private CouchRestDiscovery couchRestDiscovery;

    private Set<MappedResource> mappedResources;

    private SpelExpressionParser couchRestSpelExpressionParser;

    private Expression couchRestBaseSecurityRule;

    /**
     * Dependency injection constructor.
     *
     * @param applicationContext ApplicationContext. Used to find beans.
     * @param entityManager      EntityManager. Used to fetch managed entities.
     * @param globalObjectMapper ObjectMapper globally configured (optional). Maybe used for JSON mapping.
     */
    public CouchRestCore(ApplicationContext applicationContext,
                         EntityManager entityManager,
                         Optional<ObjectMapper> globalObjectMapper) {
        this.applicationContext = applicationContext;
        this.entityManager = entityManager;
        this.globalObjectMapper = globalObjectMapper;
    }

    /**
     * <p>
     * First fetches {@link CouchRestConfiguration} and then applies settings via
     * {@link #applyCouchRestConfiguration()}.
     * </p>
     */
    @PostConstruct
    public void setup() {
        init();

        fetchCouchRestConfiguration();
        applyCouchRestConfiguration();

        this.couchRestDiscovery = new CouchRestDiscovery(applicationContext, entityManager);

        setupMappings();
    }

    /**
     * <p>
     *     Initializes:
     *     <ul>
     *         <li>{@link #getCouchRestSpelExpressionParser()}</li>
     *     </ul>
     * </p>
     *
     */
    protected void init() {
        this.couchRestSpelExpressionParser = new SpelExpressionParser();
    }

    /**
     * Searches a bean implementing {@link CouchRestConfiguration}, validates and normalizes it via
     * {@link ValidatedAndNormalizedCouchRestConfiguration}.
     *
     * @throws NoUniqueConfigurationFoundException if no unique {@link CouchRestConfiguration} bean exists.
     * @throws NoConfigurationFoundException       if no {@link CouchRestConfiguration} bean exists.
     */
    protected void fetchCouchRestConfiguration() {
        try {
            final CouchRestConfiguration bean = applicationContext.getBean(CouchRestConfiguration.class);
            this.couchRestConfiguration = new ValidatedAndNormalizedCouchRestConfiguration(bean);

        } catch ( NoUniqueBeanDefinitionException e ) {
            throw new NoUniqueConfigurationFoundException("No unique CouchRestConfigurations found.");
        } catch ( NoSuchBeanDefinitionException e ) {
            throw new NoConfigurationFoundException("No CouchRestConfiguration found.");
        }
    }

    /**
     * Does the following:
     * <ul>
     *     <li>{@link #setupObjectMapper()}</li>
     * </ul>
     */
    protected void applyCouchRestConfiguration() {
        setupObjectMapper();
    }

    /**
     * Sets object mapper either to one specified in {@link CouchRestConfiguration}, to global one, or creates a new one.
     */
    protected void setupObjectMapper() {
        if ( couchRestConfiguration.getCouchRestObjectMapper().isPresent() ) {
            this.couchRestObjectMapper = couchRestConfiguration.getCouchRestObjectMapper().get();
        } else if ( globalObjectMapper.isPresent() ) {
            this.couchRestObjectMapper = globalObjectMapper.get();
        } else {
            this.couchRestObjectMapper = new ObjectMapper();
        }
    }

    /**
     * Tries to parse base security rule and checks if it returns a boolean value.
     */
    protected void setupBaseSecurityRule() {
        couchRestBaseSecurityRule = couchRestSpelExpressionParser.parseExpression(
                couchRestConfiguration.getBaseSecurityRule()
        );

        // TODO @peter test boolean result
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
     * Creates new {@link MappedResource} for each discovered repo. Resource path is constructed
     * by {@link #constructFullEntityResourcePathAndAssertNoPathClash(DiscoveredUnit)}.
     * Registers mapping at internal set.
     * </p>
     */
    protected void setupRepositoryMappings() {
        couchRestDiscovery.getDiscoveredCrudRepositories().forEach(discoveredRepo -> {
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
     * {@link #constructFullEntityResourcePathAndAssertNoPathClash(DiscoveredUnit)}.
     * Then mapping is registered at internal set.
     * </p>
     */
    protected void setupEntityMappings() {
        couchRestDiscovery.getDiscoveredEntities().forEach(discoveredEntity -> {

            // First check if entity is already managed by a repo
            final Optional<DiscoveredCrudRepository> discoveredRepo =
                    couchRestDiscovery.getDiscoveredCrudRepositories().stream()
                            .filter(dr -> dr.getEntityClass() == discoveredEntity.getEntityClass())
                            .findAny();

            if ( discoveredRepo.isPresent() ) {
                throw new EntityAlreadyManagedByRepositoryException(
                        "Entity '" + discoveredEntity.getEntityClass() + "' has been tagged with @CouchRest but is" +
                                " already managed by a repository which is also tagged with @CouchRest.\n" +
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
            final String newBeanName = "CouchRest-Entity-Repo-" + discoveredEntity.getEntityType().getName();
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
     * Convenience method. Prepends {@link CouchRestConfiguration#getCouchRestBasePath()} before
     *
     * @param discoveredUnit Discovered unit to create resource path for.
     * @return CouchRest base path plus resource path with trailing slash.
     */
    protected String constructFullEntityResourcePathAndAssertNoPathClash(final DiscoveredUnit discoveredUnit) {
        final String resourceName = !discoveredUnit.getTagAnnotation().resourceName().isBlank() ?
                discoveredUnit.getTagAnnotation().resourceName() :
                EntityUtil.getEntityClassNameSnakeCase(discoveredUnit.getEntityClass());

        final String path = couchRestConfiguration.getCouchRestBasePath() + PathUtil.normalizeWithTrailingSlash(resourceName);

        final Optional<MappedResource> existingMapping =
                getMappingByCouchRestResourcePath(path);

        if ( existingMapping.isPresent() ) {
            throw new ResourcePathClashException(
                    "There is a path clash for entity '" + discoveredUnit.getEntityClass() + "'. The path '" + path + "' is already taken.\n" +
                            "Existing mapping's source: " + existingMapping.get().getMappingSource().getTagAnnotationSource() + "\n" +
                            "New (attempted) mapping's source: " + discoveredUnit.getTagAnnotationSource() + "\n" +
                            "You may change the entity class name or specify different names in the CouchRest annotation using 'resourceName'."
            );
        }

        return path;
    }

    /**
     * Tries to find a {@link MappedResource} by path ({@link MappedResource#getResourcePathWithTrailingSlash()}.
     *
     * @param path Path to search for.
     * @return Mapping or empty.
     */
    protected Optional<MappedResource> getMappingByCouchRestResourcePath(final String path) {
        if ( !path.endsWith("/") ) throw new IllegalArgumentException("path must end with trailing slash");

        return mappedResources.stream()
                .filter(m -> m.getResourcePathWithTrailingSlash().equals(path))
                .findAny();
    }

    public ObjectMapper getCouchRestObjectMapper() {
        return couchRestObjectMapper;
    }

    public CouchRestConfiguration getCouchRestConfiguration() {
        return couchRestConfiguration;
    }

    public Set<MappedResource> getMappedResources() {
        return mappedResources;
    }

    public SpelExpressionParser getCouchRestSpelExpressionParser() {
        return couchRestSpelExpressionParser;
    }

    public Expression getCouchRestBaseSecurityRule() {
        return couchRestBaseSecurityRule;
    }
}
