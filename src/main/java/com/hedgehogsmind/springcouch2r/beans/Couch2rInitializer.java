package com.hedgehogsmind.springcouch2r.beans;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.configuration.ValidatedAndNormalizedCouch2rConfiguration;
import com.hedgehogsmind.springcouch2r.data.Couch2rMapping;
import lombok.RequiredArgsConstructor;
import net.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Couch2rInitializer {

    private final ApplicationContext applicationContext;

    private final Couch2rHandlerMapping couch2rHandlerMapping;

    private final EntityManager entityManager;

    private Couch2rConfiguration couch2rConfiguration;

    private Map<Object, Couch2r> couch2rBeans;

    private Map<CrudRepository, Couch2r> couch2rRepositories;

    private Map<EntityType, Couch2r> couch2rEntities;

    @PostConstruct
    public void setup() {
        fetchCouch2rConfiguration();

        // Bean stuff
        collectCouch2rBeans();
        filterCouch2rRepositories();
        validateNoBeansUnconsumed();

        setupRepositories();

        // Entity stuff
        collectCouch2rEntities();
        setupEntities();

        // TODO @peter JpaEntityInformationSupport
        // TODO @peter Retrieve global ObjectMapper Configuration?
        // TODO @peter we prefer repos > if entity has already repository > dont create twice
        //  > throw error or warning log
    }

    /**
     * Searches a bean implementing {@link Couch2rConfiguration}, validates and normalizes it via
     * {@link ValidatedAndNormalizedCouch2rConfiguration}.
     */
    protected void fetchCouch2rConfiguration() {
        try {
            final Couch2rConfiguration bean = applicationContext.getBean(Couch2rConfiguration.class);

            this.couch2rConfiguration = new ValidatedAndNormalizedCouch2rConfiguration(
                    bean
            );
        } catch ( NoUniqueBeanDefinitionException e ) {
            throw new IllegalStateException("No unique Couch2rConfigurations found."); // TODO @peter own exception
        } catch ( NoSuchBeanDefinitionException e ) {
            throw new IllegalStateException("No Couch2rConfiguration found."); // TODO @peter own exception
        }
    }

    /**
     * Finds all beans that are annotated with {@link Couch2r} and puts them into the couch2rBeans map.
     */
    protected void collectCouch2rBeans() {
        this.couch2rBeans = new HashMap<>();
        final String[] beanNames = applicationContext.getBeanNamesForAnnotation(Couch2r.class);

        for ( final String beanName : beanNames ) {
            final Object bean = applicationContext.getBean(beanName);
            final Couch2r annotation = AnnotationUtils.findAnnotation(bean.getClass(), Couch2r.class);

            couch2rBeans.put(bean, annotation);
        }
    }

    /**
     * Searches all beans that are of type {@link CrudRepository} and adds them to the dedicated map.
     */
    protected void filterCouch2rRepositories() {
        if ( this.couch2rBeans == null ) {
            throw new IllegalStateException("couch2rBeans has not been initialized yet.");
        }

        this.couch2rRepositories = new HashMap<>();

        for ( final Map.Entry<Object, Couch2r> beanMapping : this.couch2rBeans.entrySet() ) {
            if ( beanMapping.getKey() instanceof CrudRepository ) {
                this.couch2rRepositories.put((CrudRepository) beanMapping.getKey(), beanMapping.getValue());
            }
        }
    }

    /**
     * Computes the beans that are not
     * <ul>
     *     <li>CrudRepository Beans</li>
     * </ul>
     *
     * @return Map of remaining beans.
     */
    protected Map<Object, Couch2r> computeUnconsumedBeans() {
        final Map<Object, Couch2r> unconsumedBeans = new HashMap<>(this.couch2rBeans);

        this.couch2rRepositories.forEach((bean, ann) -> unconsumedBeans.remove(bean));

        return unconsumedBeans;
    }

    /**
     * Fetches unconsumed beans and throws errors containing declaration source of {@link Couch2r}.
     */
    protected void validateNoBeansUnconsumed() {
        final Map<Object, Couch2r> unconsumedBean = computeUnconsumedBeans();

        if ( !unconsumedBean.isEmpty() ) {
            final Map.Entry<Object, Couch2r> any = unconsumedBean.entrySet().stream().findAny().get();

            final Object source = getAnnotationSource(any.getKey(), Couch2r.class);

            if ( source instanceof Class ) {
                throw new IllegalStateException("@Couch2r not supported on: "+((Class)source).getName());
            } else {
                throw new IllegalStateException("@Couch2r not supported on: "+source);
            }
        }
    }

    /**
     * Fetches all entity classes from entity manager and checks, if they are annotated with {@link Couch2r}.
     * If so, they will be added to map of couch2r entities.
     */
    protected void collectCouch2rEntities() {
        couch2rEntities = new HashMap<>();

        entityManager.getMetamodel().getEntities().forEach(et -> {
            final Class<?> entityClass = et.getJavaType();
            final Couch2r couch2rAnnotation = AnnotationUtils.findAnnotation(entityClass, Couch2r.class);

            if ( couch2rAnnotation != null ) {
                this.couch2rEntities.put(et, couch2rAnnotation);
            }
        });
    }

    /**
     * Finds declaring source of annotation type of given object.
     * @param bean Bean.
     * @param annotationType Annotation class.
     * @return Source or null.
     */
    protected Object getAnnotationSource(final Object bean, final Class<? extends Annotation> annotationType) {
        return MergedAnnotations.from(bean.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES)
                .get(annotationType).getSource();
    }

    /**
     * For each found repository: Create {@link Couch2rMapping} and register it at the
     * {@link Couch2rHandlerMapping};
     */
    protected void setupRepositories() {
        if ( couch2rRepositories == null ) throw new IllegalStateException("No Couch2r repositories have been collected yet.");

        couch2rRepositories.forEach((bean, anno) -> {
            couch2rHandlerMapping.registerMapping(
                    createMappingForRepository(bean, anno)
            );
        });
    }

    /**
     * Creates path for repository using {@link Couch2rConfiguration#getCouch2rBasePath()} and
     * {@link #getEntityNameByClass(Class)} (CrudRepository)} + {@link #getEntityClassOfRepository(CrudRepository)}.
     *
     * @param repo Repository.
     * @param annotation Couch2r annotation found on repo.
     * @return New mapping.
     */
    protected Couch2rMapping createMappingForRepository(final CrudRepository repo, final Couch2r annotation) {
        final String path =
                couch2rConfiguration.getCouch2rBasePath() + "/"
                        + getEntityNameByClass(getEntityClassOfRepository(repo));

        return new Couch2rMapping(path, repo);
    }



    /**
     * For each entity: Creates {@link Couch2rMapping} and registers that at {@link Couch2rHandlerMapping}.
     */
    protected void setupEntities() {
        if ( couch2rEntities == null ) throw new IllegalStateException("No Couch2r entities have been selected yet.");

        // TODO @peter check if repo for entity is already managed
        //  >> maybe add entity class to Coch2rMapping > this way we could iterate through existing mappings?

        couch2rEntities.forEach((et, anno) -> {
            couch2rHandlerMapping.registerMapping(
                    createMappingForEntity(et, anno)
            );
        });
    }

    /**
     * Creates new {@link SimpleJpaRepository} and then creates
     * a new {@link Couch2rMapping}. Path will be created using entity class name starting
     * with lower letter.
     *
     * @param entityType EntityType of entity.
     * @param couch2rAnnotation Annotation.
     * @return New Couch2r mapping carrying new Repository instance.
     */
    protected Couch2rMapping createMappingForEntity(final EntityType entityType, final Couch2r couch2rAnnotation) {
        final SimpleJpaRepository repository = new SimpleJpaRepository(
                entityType.getJavaType(),
                entityManager
        );

        final String path =
                couch2rConfiguration.getCouch2rBasePath() + "/"
                        + getEntityNameByClass(entityType.getJavaType());

        return new Couch2rMapping(path, repository);
    }


    /**
     * Uses {@link TypeResolver} to fetch generic class type.
     * @param repository Repository.
     * @return Generic entity type of repository.
     */
    protected Class<?> getEntityClassOfRepository(final CrudRepository repository) {
        final Class<?>[] typeArgs = TypeResolver.resolveRawArguments(
                CrudRepository.class,
                repository.getClass()
        );

        if ( typeArgs.length < 2 ) throw new IllegalStateException("Could not fetch generic types properly");
        return typeArgs[0];
    }

    /**
     * Takes {@link Class#getSimpleName()} and converts first letter to lower case.
     * @param clazz Class of which entity name is wanted.
     * @return Class's simple name starting with lower letter.
     */
    protected String getEntityNameByClass(final Class<?> clazz) {
        final String rawName = clazz.getSimpleName();
        final StringBuilder sb = new StringBuilder()
                .append(rawName.substring(0, 1).toLowerCase());

        if ( rawName.length() > 1 ) sb.append(rawName.substring(1));
        return sb.toString();
    }

}
