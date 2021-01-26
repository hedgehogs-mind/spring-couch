package com.hedgehogsmind.springcouchrest.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.beans.exceptions.*;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.ValidatedAndNormalizedCouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredCrudRepository;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.util.EntityUtil;
import com.hedgehogsmind.springcouchrest.util.PathUtil;
import com.hedgehogsmind.springcouchrest.workers.discovery.CouchRestDiscovery;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import com.hedgehogsmind.springcouchrest.workers.springel.CouchRestSpelRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CouchRestCore {

    private static final Logger log = LoggerFactory.getLogger(CouchRestCore.class);

    private final ApplicationContext applicationContext;

    private final EntityManager entityManager;

    private final Optional<ObjectMapper> globalObjectMapper;

    private CouchRestConfiguration couchRestConfiguration;

    private CouchRestDiscovery couchRestDiscovery;

    private Set<MappedResource> mappedResources;

    private ObjectMapper couchRestObjectMapper;

    private Object couchRestSpelEvaluationRootObject;

    private SpelExpressionParser couchRestSpelExpressionParser;

    private StandardEvaluationContext couchRestSpelEvaluationContext;

    private Expression couchRestBaseSecurityRule;

    private Expression couchRestDefaultEndpointSecurityRule;

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
        log.info("Welcome to CouchRest!");
        log.info("Starting configuration of CouchRest.");

        init();

        fetchCouchRestConfiguration();
        applyCouchRestConfiguration();

        discoverCouchRestResources();
        setupMappings();
    }

    /**
     * <p>
     * Initializes:
     * </p>
     *
     * <ul>
     *     <li>{@link #getCouchRestSpelExpressionParser()}</li>
     *     <li>{@link #getCouchRestSpelEvaluationContext()}</li>
     * </ul>
     */
    protected void init() {
        this.couchRestSpelExpressionParser = new SpelExpressionParser();
        this.couchRestSpelEvaluationContext = new StandardEvaluationContext();
    }

    /**
     * Searches a bean implementing {@link CouchRestConfiguration}, validates and normalizes it via
     * {@link ValidatedAndNormalizedCouchRestConfiguration}.
     *
     * @throws NoUniqueConfigurationFoundException if no unique {@link CouchRestConfiguration} bean exists.
     * @throws NoConfigurationFoundException       if no {@link CouchRestConfiguration} bean exists.
     */
    protected void fetchCouchRestConfiguration() {
        log.info("Trying to obtain CouchRestConfiguration bean.");

        try {
            final CouchRestConfiguration bean = applicationContext.getBean(CouchRestConfiguration.class);
            this.couchRestConfiguration = new ValidatedAndNormalizedCouchRestConfiguration(bean);

            log.info("CouchRestConfiguration bean successfully fetched.");

        } catch (NoUniqueBeanDefinitionException e) {
            throw new NoUniqueConfigurationFoundException("No unique CouchRestConfigurations found.");
        } catch (NoSuchBeanDefinitionException e) {
            throw new NoConfigurationFoundException("No CouchRestConfiguration found.");
        }
    }

    /**
     * Does the following:
     * <ul>
     *     <li>{@link #setupObjectMapper()}</li>
     *     <li>{@link #setupSpringElEvaluationRootObject()}</li>
     *     <li>{@link #setupBaseSecurityRule()}</li>
     *     <li>{@link #setupDefaultSecurityRule()}</li>
     * </ul>
     */
    protected void applyCouchRestConfiguration() {
        log.info("Applying CouchRestConfiguration.");

        setupObjectMapper();
        setupSpringElEvaluationRootObject();
        setupBaseSecurityRule();
        setupDefaultSecurityRule();
    }

    /**
     * Sets object mapper either to one specified in {@link CouchRestConfiguration}, to global one, or creates a new one.
     */
    protected void setupObjectMapper() {
        if (couchRestConfiguration.getCouchRestObjectMapper().isPresent()) {
            log.info("Applying Jackson ObjectMapper specified in CouchRestConfiguration.");
            this.couchRestObjectMapper = couchRestConfiguration.getCouchRestObjectMapper().get();

        } else if (globalObjectMapper.isPresent()) {
            log.info("Applying global Jackson ObjectMapper obtained via Spring dependency injection.");
            this.couchRestObjectMapper = globalObjectMapper.get();

        } else {
            log.info("Instantiating new blank Jackson ObjectMapper.");
            this.couchRestObjectMapper = new ObjectMapper();

        }
    }

    /**
     * Sets spring el root object for evaluation. Prefers root object from
     * {@link CouchRestConfiguration}. If not given, instantiates a
     * {@link CouchRestSpelRoot} instance.
     * Applies root object to {@link #getCouchRestSpelEvaluationContext()}.
     */
    protected void setupSpringElEvaluationRootObject() {
        final Optional<Object> configurationSpelRootObject = couchRestConfiguration.getSpringElEvaluationRootObject();

        if ( configurationSpelRootObject.isPresent() ) {
            log.info("Applying SpringEL root object specified in CouchRestConfiguration.");
            this.couchRestSpelEvaluationRootObject = configurationSpelRootObject.get();

        } else {
            log.info("Instantiating new blank SpringEL root object of type CouchRestSpelRoot.");
            this.couchRestSpelEvaluationRootObject = new CouchRestSpelRoot();

        }

        log.info("Autowiring SpringEL root object.");
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this.couchRestSpelEvaluationRootObject);

        log.info("Applying root object to SpringEL evaluation context.");
        this.couchRestSpelEvaluationContext.setRootObject(
                this.couchRestSpelEvaluationRootObject
        );
    }

    /**
     * Tries to parse base security rule and checks if it returns a boolean value.
     */
    protected void setupBaseSecurityRule() {
        log.info("Parsing base security rule specified in CouchRestConfiguration.");
        couchRestBaseSecurityRule = couchRestSpelExpressionParser.parseExpression(
                couchRestConfiguration.getBaseSecurityRule()
        );

        log.info("Testing base security rule for boolean result.");
        final Object testResult = this.couchRestBaseSecurityRule.getValue(couchRestSpelEvaluationContext);

        if (!(testResult instanceof Boolean)) {
            throw new BaseSecurityRuleDoesNotReturnBooleanValueException(
                    "Base security rule did not return boolean result. Instead it was of type: " +
                            (testResult != null ? testResult.getClass() : "null")
            );
        }
        log.info("Base security rule is fine.");
    }


    /**
     * Tries to parse default endpoint security rule and checks if it returns a boolean value.
     */
    protected void setupDefaultSecurityRule() {
        log.info("Parsing default endpoint security rule specified in CouchRestConfiguration.");
        couchRestDefaultEndpointSecurityRule = couchRestSpelExpressionParser.parseExpression(
                couchRestConfiguration.getDefaultEndpointSecurityRule()
        );

        log.info("Testing default endpoint security rule for boolean result.");
        final Object testResult = this.couchRestDefaultEndpointSecurityRule.getValue(couchRestSpelEvaluationContext);

        if (!(testResult instanceof Boolean)) {
            throw new DefaultSecurityRuleDoesNotReturnBooleanValueException(
                    "Default endpoint security rule did not return boolean result. Instead it was of type: " +
                            (testResult != null ? testResult.getClass() : "null")
            );
        }
        log.info("Default endpoint security rule is fine.");
    }

    /**
     * Creates new {@link CouchRestDiscovery} with current {@link ApplicationContext} and {@link EntityManager}.
     */
    protected void discoverCouchRestResources() {
        log.info("Discovering CouchRest resources.");
        this.couchRestDiscovery = new CouchRestDiscovery(applicationContext, entityManager);

        log.info("Nr. of CouchRest repositories found: {}", this.couchRestDiscovery.getDiscoveredCrudRepositories().size());
        log.info("Nr. of CouchRest entities found: {}", this.couchRestDiscovery.getDiscoveredEntities().size());
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
        if ( couchRestDiscovery.getDiscoveredCrudRepositories().isEmpty() ) {
            log.info("Skipping CRUD repo mapping creation, because no repo has been found for REST exposure.");

        } else {
            log.info("Creating resource mappings for CRUD repositories.");

            couchRestDiscovery.getDiscoveredCrudRepositories().forEach(discoveredRepo -> {
                final MappedEntityResource newRepositoryMapping = new MappedEntityResource(
                        this,
                        discoveredRepo,
                        constructFullEntityResourcePathAndAssertNoPathClash(discoveredRepo),
                        discoveredRepo.getEntityType(),
                        discoveredRepo.getCrudRepository()
                );

                newRepositoryMapping.setup();

                mappedResources.add(newRepositoryMapping);
            });

            log.info("Creation of CRUD repository mappings finished.");
        }
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
        if ( couchRestDiscovery.getDiscoveredEntities().isEmpty() ) {
            log.info("Skipping entity mapping creation, because no entity has been found for REST exposure.");

        } else {
            log.info("Creating resource mappings for entities.");

            couchRestDiscovery.getDiscoveredEntities().forEach(discoveredEntity -> {

                // First check if entity is already managed by a repo
                final Optional<DiscoveredCrudRepository> discoveredRepo =
                        couchRestDiscovery.getDiscoveredCrudRepositories().stream()
                                .filter(dr -> dr.getEntityClass() == discoveredEntity.getEntityClass())
                                .findAny();

                if (discoveredRepo.isPresent()) {
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
                        this,
                        discoveredEntity,
                        constructFullEntityResourcePathAndAssertNoPathClash(discoveredEntity),
                        discoveredEntity.getEntityType(),
                        finishedRepoBean
                );

                newEntityMapping.setup();

                mappedResources.add(newEntityMapping);
            });

            log.info("Creation of entity mappings finished.");
        }
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

        if (existingMapping.isPresent()) {
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
        if (!path.endsWith("/")) throw new IllegalArgumentException("path must end with trailing slash");

        return mappedResources.stream()
                .filter(m -> m.getResourcePathWithTrailingSlash().equals(path))
                .findAny();
    }

    /**
     * Convenience method. Parses given expression using {@link #getCouchRestSpelExpressionParser()}.
     *
     * @param expression Expression.
     * @return Parsed expression.
     */
    public Expression parseSpelExpression(final String expression) {
        return this.couchRestSpelExpressionParser.parseExpression(expression);
    }

    /**
     * Evaluates expression using {@link #getCouchRestSpelEvaluationContext()} as context.
     *
     * @param expression Expression to evaluate.
     * @return Returned value.
     */
    public Object evaluateExpression(final Expression expression) {
        return expression.getValue(couchRestSpelEvaluationContext);
    }

    /**
     * Evaluates expression using {@link #getCouchRestSpelEvaluationContext()} as context
     * and returns instance of type returnType.
     *
     * @param expression Expression to evaluate.
     * @param returnType Expected return type.
     * @param <R>        Type of return type.
     * @return Return value.
     */
    public <R> R evaluateExpression(final Expression expression, final Class<? extends R> returnType) {
        return expression.getValue(couchRestSpelEvaluationContext, returnType);
    }

    /**
     * Convenience method. Calls {@link #evaluateExpression(Expression, Class)} with
     * {@link #getCouchRestBaseSecurityRule()} as expression.
     *
     * @return Result of base security rule.
     */
    public boolean evaluateBaseSecurityRule() {
        return evaluateExpression(couchRestBaseSecurityRule, Boolean.class);
    }

    /**
     * Convenience method. Calls {@link #evaluateExpression(Expression, Class)} with
     * {@link #getCouchRestDefaultEndpointSecurityRule()} as expression.
     *
     * @return Result of default endpoint security rule.
     */
    public boolean evaluateDefaultEndpointSecurityRule() {
        return evaluateExpression(couchRestDefaultEndpointSecurityRule, Boolean.class);
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

    public Expression getCouchRestDefaultEndpointSecurityRule() {
        return couchRestDefaultEndpointSecurityRule;
    }

    public Object getCouchRestSpelEvaluationRootObject() {
        return couchRestSpelEvaluationRootObject;
    }

    public StandardEvaluationContext getCouchRestSpelEvaluationContext() {
        return couchRestSpelEvaluationContext;
    }
}
