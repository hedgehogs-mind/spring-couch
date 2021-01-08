package com.hedgehogsmind.springcouchrest.workers.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredCrudRepository;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredEntity;
import com.hedgehogsmind.springcouchrest.data.discovery.DiscoveredUnit;
import com.hedgehogsmind.springcouchrest.util.AnnotationUtil;
import com.hedgehogsmind.springcouchrest.util.EntityUtil;
import com.hedgehogsmind.springcouchrest.util.RepositoryUtil;
import com.hedgehogsmind.springcouchrest.workers.discovery.exceptions.UnsupportedBeanTypeTaggedException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Indexes all {@link DiscoveredUnit} on
 * instantiation.
 */
public class CouchRestDiscovery {

    private final Set<DiscoveredCrudRepository> discoveredCrudRepositories;

    private final Set<DiscoveredEntity> discoveredEntities;

    public CouchRestDiscovery(
            final ApplicationContext applicationContext,
            final EntityManager entityManager
    ) {
        this.discoveredCrudRepositories = new HashSet<>();
        this.discoveredEntities = new HashSet<>();

        // TODO @peter add repository class to discovered element

        discoverCouchRestBeans(applicationContext, entityManager);
        discoverCouchRestEntities(entityManager);
    }

    /**
     * <p>
     *     Fetches all beans with {@link CouchRest} annotation and calls {@link #addBean(Object, EntityManager)} for each
     *     single bean.
     * </p>
     * @param applicationContext Used to retrieve beans.
     * @param entityManager Uses for creating {@link DiscoveredUnit}s.
     */
    protected void discoverCouchRestBeans(
            final ApplicationContext applicationContext,
            final EntityManager entityManager
    ) {
        final Map<String, Object> couchRestBeans = applicationContext.getBeansWithAnnotation(CouchRest.class);
        couchRestBeans.values().forEach(bean -> addBean(bean, entityManager));
    }

    /**
     * <p>
     *     Depending on the type of the bean, differed {@link DiscoveredUnit}
     *     instances will be created and added to internal sets.
     * </p>
     *
     * <p>
     *     Currently supported beans:
     *     <ul>
     *         <li>{@link CrudRepository}</li>
     *     </ul>
     * </p>
     *
     * <p>
     *     If a bean type is not supported, this method throws a {@link UnsupportedBeanTypeTaggedException}.
     * </p>
     *
     * @param bean Bean to add as discovered unit.
     * @param entityManager EntityManager for {@link javax.persistence.metamodel.EntityType} retrieval.
     * @throws UnsupportedBeanTypeTaggedException if bean is not supported.
     */
    protected void addBean(
            final Object bean,
            final EntityManager entityManager
    ) {
        if ( bean instanceof CrudRepository ) {

            discoveredCrudRepositories.add(
                    beanToDiscoveredCrudRepository(
                            (CrudRepository) bean,
                            entityManager
                    )
            );

        } else {
            throw new UnsupportedBeanTypeTaggedException(
                    "@CouchRest not supported for bean of type "+bean.getClass()
            );
        }
    }

    /**
     * <p>
     *     Fetches entity class, entity type, couch rest annotation and couch rest modifier annos and creates new
     *     discovered instance.
     * </p>
     *
     * @param crudRepository CrudRepository to created discovered unit instance for.
     * @param entityManager EntityManager. Used for entity type retrieval.
     * @return New CrudRepo discovered unit instance.
     */
    protected DiscoveredCrudRepository beanToDiscoveredCrudRepository(
            final CrudRepository crudRepository,
            final EntityManager entityManager
    ) {
        final Class<?> entityClassOfRepository =
                RepositoryUtil.getEntityClassOfRepositoryClass(crudRepository.getClass());

        final AnnotationUtil.AnnotationOccurrence<CouchRest> tagAnnotation =
                AnnotationUtil.getRequiredAnnotation(crudRepository.getClass(), CouchRest.class);

        return new DiscoveredCrudRepository(
                tagAnnotation.getAnnotation(),
                tagAnnotation.getSource(),
                AnnotationUtil.getAllCouchRestModifierAnnotations(crudRepository.getClass()),
                entityClassOfRepository,
                EntityUtil.getRequiredEntityTypeByEntityClass(entityClassOfRepository, entityManager),
                crudRepository
        );
    }

    /**
     * Fetches all entity types from EntityManager. For each: Checks if {@link CouchRest} is present.
     * If so, a new {@link DiscoveredEntity} instance is created and added to the set of
     * discovered entities.
     *
     * @param entityManager EntityManager used to fetch all managed entities.
     */
    protected void discoverCouchRestEntities(
            final EntityManager entityManager
    ) {
        entityManager.getMetamodel().getEntities().forEach(et -> {
            final Optional<AnnotationUtil.AnnotationOccurrence<CouchRest>> tagAnnotation =
                    Optional.ofNullable(
                            AnnotationUtil.getAnnotation(
                                    et.getJavaType(),
                                    CouchRest.class
                            )
                    );

            if ( tagAnnotation.isPresent() ) {
                discoveredEntities.add(
                        new DiscoveredEntity(
                                tagAnnotation.get().getAnnotation(),
                                tagAnnotation.get().getSource(),
                                AnnotationUtil.getAllCouchRestModifierAnnotations(et.getJavaType()),
                                et.getJavaType(),
                                et
                        )
                );
            }
        });
    }

    public Set<DiscoveredCrudRepository> getDiscoveredCrudRepositories() {
        return discoveredCrudRepositories;
    }

    public Set<DiscoveredEntity> getDiscoveredEntities() {
        return discoveredEntities;
    }
}
