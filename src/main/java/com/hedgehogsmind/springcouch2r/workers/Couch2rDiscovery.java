package com.hedgehogsmind.springcouch2r.workers;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredCrudRepository;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredEntity;
import com.hedgehogsmind.springcouch2r.util.Couch2rAnnotationUtil;
import com.hedgehogsmind.springcouch2r.util.Couch2rEntityUtil;
import com.hedgehogsmind.springcouch2r.util.Couch2rRepositoryUtil;
import com.hedgehogsmind.springcouch2r.workers.exceptions.Couch2rUnsupportedBeanTypeFound;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Indexes all {@link com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit} on
 * instantiation.
 */
public class Couch2rDiscovery {

    private final Set<Couch2rDiscoveredCrudRepository> discoveredCrudRepositories;

    private final Set<Couch2rDiscoveredEntity> discoveredEntities;

    public Couch2rDiscovery(
            final ApplicationContext applicationContext,
            final EntityManager entityManager
    ) {
        this.discoveredCrudRepositories = new HashSet<>();
        this.discoveredEntities = new HashSet<>();

        discoverCouch2rBeans(applicationContext, entityManager);

        // TODO @peter discover entities
    }

    /**
     * <p>
     *     Fetches all beans with {@link Couch2r} annotation and calls {@link #addBean(Object, EntityManager)} for each
     *     single bean.
     * </p>
     * @param applicationContext Used to retrieve beans.
     * @param entityManager Uses for creating {@link com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit}s.
     */
    protected void discoverCouch2rBeans(
            final ApplicationContext applicationContext,
            final EntityManager entityManager
    ) {
        final Map<String, Object> couch2rBeans = applicationContext.getBeansWithAnnotation(Couch2r.class);
        couch2rBeans.values().forEach(bean -> addBean(bean, entityManager));
    }

    /**
     * <p>
     *     Depending on the type of the bean, differed {@link com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit}
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
     *     If a bean type is not supported, this method throws a {@link Couch2rUnsupportedBeanTypeFound}.
     * </p>
     *
     * @param bean Bean to add as discovered unit.
     * @param entityManager EntityManager for {@link javax.persistence.metamodel.EntityType} retrieval.
     * @throws Couch2rUnsupportedBeanTypeFound if bean is not supported.
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
            throw new Couch2rUnsupportedBeanTypeFound(
                    "@Couch2r not supported for bean of type "+bean.getClass()
            );
        }
    }

    /**
     * <p>
     *     Fetches entity class, entity type, couch2r annotation and couch2r modified annos and creates new
     *     discovered instance.
     * </p>
     *
     * @param crudRepository CrudRepository to created discovered unit instance for.
     * @param entityManager EntityManager. Used for entity type retrieval.
     * @return New CrudRepo discovered unit instance.
     */
    protected Couch2rDiscoveredCrudRepository beanToDiscoveredCrudRepository(
            final CrudRepository crudRepository,
            final EntityManager entityManager
    ) {
        final Class<?> entityClassOfRepository = Couch2rRepositoryUtil.getEntityClassOfRepositoryClass(crudRepository.getClass());

        return new Couch2rDiscoveredCrudRepository(
                Couch2rAnnotationUtil.getRequiredAnnotation(crudRepository.getClass(), Couch2r.class),
                Couch2rAnnotationUtil.getAllCouch2rModifierAnnotations(crudRepository.getClass()),
                entityClassOfRepository,
                Couch2rEntityUtil.getRequiredEntityTypeByEntityClass(entityClassOfRepository, entityManager),
                crudRepository
        );
    }

}
