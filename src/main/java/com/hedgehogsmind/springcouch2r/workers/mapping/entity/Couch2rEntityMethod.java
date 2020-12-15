package com.hedgehogsmind.springcouch2r.workers.mapping.entity;

import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rResourceMethod;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.exceptions.Couch2rIdTypeParsingNotSupportedException;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.exceptions.Couch2rIdValueNotParsableException;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.EntityType;

/**
 * Can be used for {@link Couch2rEntityMapping}. This class provides some utility methods
 * for the extending class, like accessing the repository, the entity ID type or parsing a
 * string as the entity's ID.
 */
public abstract class Couch2rEntityMethod implements Couch2rResourceMethod {

    private final Couch2rEntityMapping entityMappingParent;

    public Couch2rEntityMethod(Couch2rEntityMapping entityMappingParent) {
        this.entityMappingParent = entityMappingParent;
    }

    /**
     * Delivers the parent for this entity method.
     * @return Parent of this method.
     */
    protected Couch2rEntityMapping getParentEntityMapping() {
        return entityMappingParent;
    }

    /**
     * Delivers entity type of handled entity.
     *
     * @return Entity type of mapped entity.
     */
    protected EntityType getEntityType() {
        return entityMappingParent.getEntityType();
    }

    /**
     * Returns class of entity.
     * @return Entity class.
     */
    protected Class<?> getEntityClass() {
        return getEntityType().getJavaType();
    }

    /**
     * Delivers class of entity's ID type.
     * @return ID type of mapped entity.
     */
    protected Class<?> getEntityIdClass() {
        return getEntityType().getIdType().getJavaType();
    }

    /**
     * Delivers repository for entity of type {@link #getEntityClass()}.
     * @return Repository.
     */
    protected CrudRepository getRepository() {
        return entityMappingParent.getRepository();
    }

    /**
     * Tries to parse ID by type {@link #getEntityIdClass()}. Currently supports:
     * <ul>
     *     <li>{@link Long}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link String}</li>
     * </ul>
     *
     * @param value Id value as string.
     * @return ID as parsed object.
     */
    protected Object parseId(final String value) {
        final Class idClass = getEntityIdClass();

        if ( idClass == Long.class || idClass == long.class )
            return parseLongId(value);

        else if ( idClass == Integer.class || idClass == int.class )
            return parseIntegerId(value);

        else if ( idClass == String.class )
            return parseStringId(value);

        else
            throw new Couch2rIdTypeParsingNotSupportedException(idClass);
    }

    /**
     * Tries to parse value as Long. If not possible, a {@link Couch2rIdValueNotParsableException}
     * will be thrown.
     * @param value Value to be parsed.
     * @return Value as Long.
     */
    protected Long parseLongId(final String value) {
        try {
            return Long.parseLong(value);
        } catch ( NumberFormatException e ) {
            throw new Couch2rIdValueNotParsableException("Id not a number", e);
        }
    }

    /**
     * Tries to parse value as Integer. If not possible, a {@link Couch2rIdValueNotParsableException}
     * will be thrown.
     * @param value Value to be parsed.
     * @return Value as Integer.
     */
    protected Integer parseIntegerId(final String value) {
        try {
            return Integer.parseInt(value);
        } catch ( NumberFormatException e ) {
            throw new Couch2rIdValueNotParsableException("ID not a number", e);
        }
    }


    /**
     * Just returns value.
     * @param value Value to be parsed.
     * @return Value.
     */
    protected String parseStringId(final String value) {
        return value;
    }

}
