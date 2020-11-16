package com.hedgehogsmind.springcouch2r.util;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.Optional;

public final class Couch2rEntityUtil {

    private Couch2rEntityUtil() {}

    /**
     * Tries to fetch entity type from entity manager by entity class.
     * @param entityClass Entity class.
     * @param entityManager Entity manager used for search.
     * @return EntityType of empty.
     */
    public static Optional<EntityType<?>> getEntityTypeByEntityClass(final Class<?> entityClass, final EntityManager entityManager) {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(et -> et.getJavaType() == entityClass)
                .findAny();
    }

    /**
     * Calls {@link #getEntityTypeByEntityClass(Class, EntityManager)}. If no type found, an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param entityClass Entity class to fetch type for.
     * @param entityManager Entity manager used for search.
     * @return EntityType of entity class.
     */
    public static EntityType<?> getRequiredEntityTypeByEntityClass(final Class<?> entityClass, final EntityManager entityManager) {
        final Optional<EntityType<?>> optionalEntityType = getEntityTypeByEntityClass(entityClass, entityManager);

        if ( optionalEntityType.isEmpty() )
            throw new IllegalArgumentException("Entity class "+entityClass+" not managed my entity manager");

        return optionalEntityType.get();
    }

}
