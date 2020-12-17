package com.hedgehogsmind.springcouch2r.workers.mapping.entity;

import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rResourceMethod;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.exceptions.Couch2rIdTypeParsingNotSupportedException;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.exceptions.Couch2rIdValueNotParsableException;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

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

    /**
     * Fetches Id member of mapped entity.
     * @return Member.
     */
    protected Member getIdMember() {
        return getEntityType().getId(getEntityIdClass()).getJavaMember();
    }

    /**
     * Searches for a method with a name like "setXYZ(idValue)" where XYZ corresponds to the
     * id attribute name. The check is performed case insensitive.
     *
     * @return Getter method or empty if no (visible) method is available.
     */
    protected Optional<Method> getIdSetter() {
        final Member idMember = getIdMember();
        final String getterName = "set"+idMember.getName();

        return Arrays.stream(getEntityClass().getMethods())
                .filter(method -> method.getName().equalsIgnoreCase(getterName) && method.getParameterCount() == 1)
                .findAny();
    }

    /**
     * <p>
     *     Retrieves id value of the given entity instance which must assignable to
     *     type {@link #getEntityClass()}.
     * </p>
     *
     * <p>
     *     Currently only supports id fields and getters.
     * </p>
     *
     * @param entity Entity instance to get its current id value.
     * @return Value of entity's id field.
     */
    protected Object getIdValue(final Object entity) {
        if ( !getEntityClass().isInstance(entity) ) {
            throw new IllegalArgumentException("Given entity is not of type "+getEntityClass());
        }

        final Member idMember = getIdMember();

        try {
            if ( idMember instanceof Field ) {
                final Field idField = (Field) idMember;
                return idField.get(entity);

            } else if ( idMember instanceof Method ) {
                final Method idMethod = (Method) idMember;

                if ( idMethod.getParameterCount() > 0 ) {
                    throw new IllegalStateException("Can not fetch id value of entity type "+getEntityClass()+"." +
                            " It has id member of type method which is not a plain getter without any args.");
                }

                return idMethod.invoke(entity);

            } else {
                throw new RuntimeException("Unable to get idValue for entity "+getEntityClass()+" with id member of type "
                        +idMember.getClass());
            }
        } catch ( IllegalAccessException | InvocationTargetException e ) {
            throw new RuntimeException("Was unable to fetch id value for an entity of type "+getEntityClass(), e);
        }
    }

    /**
     * <p>
     *     First tries to set the id value of the given entity via a setter method.
     *     If no setter method exists, we try to set the id value direclty via the field.
     * </p>
     *
     * @param entity Entity instance whose id value shall be set.
     * @param value Value of id.
     */
    public void setIdValue(final Object entity, final Object value) {
        if ( !getEntityClass().isInstance(entity) ) {
            throw new IllegalArgumentException("Given entity is not of type "+getEntityClass());
        }

        final Optional<Method> idSetter = getIdSetter();

        if ( idSetter.isPresent() ) {
            try {
                idSetter.get().invoke(entity, value);
            } catch ( IllegalAccessException | InvocationTargetException e ) {
                throw new RuntimeException("Error while trying to set id value for an entity of type "+getEntityClass(), e);
            }
        } else {
            // try to set via field
            final Member idMember = getIdMember();

            if ( !(idMember instanceof Field) ) {
                throw new IllegalStateException("Can not set id value for an entity of type "+getEntityClass()+". " +
                        "Entity has neither an id setter nor an id member of type field.");
            }

            final Field idField = (Field) idMember;

            try {
                idField.set(entity, value);
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException("Error while trying to set id value for an entity of type "+
                        getEntityClass()+" by setting the id field directly.", e);
            }

        }
    }

}
