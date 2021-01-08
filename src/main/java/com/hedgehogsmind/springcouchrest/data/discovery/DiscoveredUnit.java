package com.hedgehogsmind.springcouchrest.data.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.CouchRestModifierAnnotation;
import org.springframework.core.annotation.MergedAnnotation;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Represents a unit/element which was tagged for CouchRest management
 * (using the annotation {@link CouchRest}).
 * This may be an entity or a repository.
 */
public abstract class DiscoveredUnit {

    private final CouchRest tagAnnotation;

    private final Object tagAnnotationSource;

    private final List<Annotation> couchRestModifierAnnotations;

    private final Class<?> entityClass;

    private final EntityType entityType;

    /**
     * Stores given values.
     *
     * @param tagAnnotation The annotation which lead the discovery to index this unit/element.
     * @param tagAnnotationSource {@link MergedAnnotation#getSource()} of tagAnnotation.
     * @param couchRestModifierAnnotations All other annotations on source, which are annotated with {@link CouchRestModifierAnnotation}
     * @param entityClass Class of entity. Either discovered entity class itself or entity class a repository handles.
     * @param entityType JPA entity type of entity. May fetched through the entity manager.
     */
    public DiscoveredUnit(CouchRest tagAnnotation, Object tagAnnotationSource, List<Annotation> couchRestModifierAnnotations, Class<?> entityClass, EntityType entityType) {
        this.tagAnnotation = tagAnnotation;
        this.tagAnnotationSource = tagAnnotationSource;
        this.couchRestModifierAnnotations = couchRestModifierAnnotations;
        this.entityClass = entityClass;
        this.entityType = entityType;
    }

    /**
     * Represents the initial annotation which lead the discovery to index this unit/element.
     *
     * @return CouchRest tag annotation.
     */
    public CouchRest getTagAnnotation() {
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
     * Returns all other CouchRest annotations (which are annotated with
     * {@link CouchRestModifierAnnotation}).
     *
     * @return Other annotations.
     */
    public List<Annotation> getCouchRestModifierAnnotations() {
        return couchRestModifierAnnotations;
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
        DiscoveredUnit that = (DiscoveredUnit) o;
        return Objects.equals(tagAnnotation, that.tagAnnotation) &&
                Objects.equals(tagAnnotationSource, that.tagAnnotationSource) &&
                Objects.equals(couchRestModifierAnnotations, that.couchRestModifierAnnotations) &&
                Objects.equals(entityClass, that.entityClass) &&
                Objects.equals(entityType, that.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagAnnotation, tagAnnotationSource, couchRestModifierAnnotations, entityClass, entityType);
    }

    @Override
    public String toString() {
        return "CouchRestDiscoveredUnit{" +
                "tagAnnotation=" + tagAnnotation +
                ", tagAnnotationSource=" + tagAnnotationSource +
                ", couchRestModifierAnnotations=" + couchRestModifierAnnotations +
                ", entityClass=" + entityClass +
                ", entityType=" + entityType +
                '}';
    }
}
