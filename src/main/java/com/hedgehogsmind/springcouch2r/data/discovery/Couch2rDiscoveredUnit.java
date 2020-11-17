package com.hedgehogsmind.springcouch2r.data.discovery;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.annotations.Couch2rModifierAnnotation;
import org.springframework.core.annotation.MergedAnnotation;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Represents a unit/element which was tagged for Couch2r management
 * (using the annotation {@link com.hedgehogsmind.springcouch2r.annotations.Couch2r}).
 * This may be an entity or a repository.
 */
public abstract class Couch2rDiscoveredUnit {

    private final Couch2r tagAnnotation;

    private final Object tagAnnotationSource;

    private final List<Annotation> couch2rModifierAnnotations;

    private final Class<?> entityClass;

    private final EntityType entityType;

    /**
     * Stores given values.
     *
     * @param tagAnnotation The annotation which lead the discovery to index this unit/element.
     * @param tagAnnotationSource {@link MergedAnnotation#getSource()} of tagAnnotation.
     * @param couch2rModifierAnnotations All other annotations on source, which are annotated with {@link Couch2rModifierAnnotation}
     * @param entityClass Class of entity. Either discovered entity class itself or entity class a repository handles.
     * @param entityType JPA entity type of entity. May fetched through the entity manager.
     */
    public Couch2rDiscoveredUnit(Couch2r tagAnnotation, Object tagAnnotationSource, List<Annotation> couch2rModifierAnnotations, Class<?> entityClass, EntityType entityType) {
        this.tagAnnotation = tagAnnotation;
        this.tagAnnotationSource = tagAnnotationSource;
        this.couch2rModifierAnnotations = couch2rModifierAnnotations;
        this.entityClass = entityClass;
        this.entityType = entityType;
    }

    /**
     * Represents the initial annotation which lead the discovery to index this unit/element.
     *
     * @return Couch2r tag annotation.
     */
    public Couch2r getTagAnnotation() {
        return tagAnnotation;
    }

    /**
     * Represents the source of the tagAnnotation. For more details, see
     * {@link MergedAnnotation#getSource()}.
     *
     * @return Source of tag annotation.
     */
    public Object getTagAnnotationSource() {
        return tagAnnotationSource;
    }

    /**
     * Returns all other Couch2r annotations (which are annotated with
     * {@link Couch2rModifierAnnotation}).
     *
     * @return Other annotations.
     */
    public List<Annotation> getCouch2rModifierAnnotations() {
        return couch2rModifierAnnotations;
    }

    /**
     * Represents the class of the actual entity class
     * (in case this unit is a repo: ...which is managed by the repository).
     *
     * @return Class of managed entity.
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * JPA counterpart for {@link #getEntityClass()}. Should be available in the entity manager.
     *
     * @return JPA EntityType.
     */
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couch2rDiscoveredUnit that = (Couch2rDiscoveredUnit) o;
        return Objects.equals(tagAnnotation, that.tagAnnotation) &&
                Objects.equals(tagAnnotationSource, that.tagAnnotationSource) &&
                Objects.equals(couch2rModifierAnnotations, that.couch2rModifierAnnotations) &&
                Objects.equals(entityClass, that.entityClass) &&
                Objects.equals(entityType, that.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagAnnotation, tagAnnotationSource, couch2rModifierAnnotations, entityClass, entityType);
    }

    @Override
    public String toString() {
        return "Couch2rDiscoveredUnit{" +
                "tagAnnotation=" + tagAnnotation +
                ", tagAnnotationSource=" + tagAnnotationSource +
                ", couch2rModifierAnnotations=" + couch2rModifierAnnotations +
                ", entityClass=" + entityClass +
                ", entityType=" + entityType +
                '}';
    }
}
