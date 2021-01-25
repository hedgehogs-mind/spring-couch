package com.hedgehogsmind.springcouchrest.data.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.CouchRestModifierAnnotation;
import com.hedgehogsmind.springcouchrest.data.discovery.exceptions.MultipleCouchRestModifierAnnotationsFoundException;
import com.hedgehogsmind.springcouchrest.data.discovery.exceptions.RequiredCouchRestModifierAnnotationNotFoundException;
import org.springframework.core.annotation.MergedAnnotation;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Basically returns {@link #getCouchRestModifierAnnotations()} and filters these annotations. Only those
     * which are an instance of the given annotationType will be returned.
     *
     * @param annotationType Expected annotations' type.
     * @return List of couch rest modifier annotation with the given annotation type.
     */
    public <T extends Annotation> List<T> getCouchRestModifierAnnotations(final Class<? extends T> annotationType) {
        return this.couchRestModifierAnnotations.stream()
                .filter(annotationType::isInstance)
                .map(annotation -> (T) annotation)
                .collect(Collectors.toList());
    }

    /**
     * Tries to fetch modifier annotation of given type. In case there are more than one, a
     * {@link MultipleCouchRestModifierAnnotationsFoundException} will be thrown.
     *
     * @param annotationType Type of expected annotation.
     * @return Annotation of empty if not present.
     * @throws MultipleCouchRestModifierAnnotationsFoundException if there are more than one annotation instances.
     */
    public <T extends Annotation> Optional<T> getOptionalCouchRestModifierAnnotation(final Class<? extends T> annotationType) {
        final List<T> foundAnnotations = getCouchRestModifierAnnotations(annotationType);

        if ( foundAnnotations.size() > 1 ) {
            throw new MultipleCouchRestModifierAnnotationsFoundException("Found more than one CouchRest modifier " +
                    "annotation of type '"+annotationType+"'. Expected no or only one annotation " +
                    "of that type. Discovered CouchRest element: "+tagAnnotationSource);

        } else if ( foundAnnotations.size() == 1 ) {
            return Optional.of(foundAnnotations.get(0));

        } else {
            return Optional.empty();
        }
    }

    /**
     * Fetches annotation via {@link #getOptionalCouchRestModifierAnnotation(Class)}. In case the annotation is not
     * present, a {@link RequiredCouchRestModifierAnnotationNotFoundException} will be thrown.
     * @param annotationType Type of required annotation.
     * @return Annotation instance of the given type.
     * @throws RequiredCouchRestModifierAnnotationNotFoundException if the required annotation is not present.
     */
    public <T extends Annotation> T getRequiredCouchRestModifierAnnotation(final Class<? extends T> annotationType) {
        final Optional<T> foundAnnotation = getOptionalCouchRestModifierAnnotation(annotationType);

        if ( foundAnnotation.isEmpty() ) {
            throw new RequiredCouchRestModifierAnnotationNotFoundException("Found no CouchRest modifier annotation " +
                    "of type '"+annotationType+"', but was required. " +
                    "Discovered CouchRest element: "+tagAnnotationSource);
        }

        return foundAnnotation.get();
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
