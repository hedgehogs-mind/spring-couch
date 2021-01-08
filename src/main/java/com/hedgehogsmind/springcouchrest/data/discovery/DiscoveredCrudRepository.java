package com.hedgehogsmind.springcouchrest.data.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Represents a {@link CrudRepository} which has been tagged
 * with {@link CouchRest}.
 */
public class DiscoveredCrudRepository
        extends DiscoveredUnit {

    private final CrudRepository crudRepository;

    /**
     * Stores values.
     * @param tagAnnotation See super.
     * @param tagAnnotationSource See super.
     * @param couchRestModifierAnnotations See super.
     * @param entityClass See super.
     * @param entityType See super.
     * @param crudRepository Discovered CrudRepository.
     */
    public DiscoveredCrudRepository(CouchRest tagAnnotation, Object tagAnnotationSource, List<Annotation> couchRestModifierAnnotations, Class<?> entityClass, EntityType entityType, CrudRepository crudRepository) {
        super(tagAnnotation, tagAnnotationSource, couchRestModifierAnnotations, entityClass, entityType);
        this.crudRepository = crudRepository;
    }

    /**
     * Discovered CrudRepository.
     * @return Discovered Repo.
     */
    public CrudRepository getCrudRepository() {
        return crudRepository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DiscoveredCrudRepository that = (DiscoveredCrudRepository) o;
        return Objects.equals(crudRepository, that.crudRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), crudRepository);
    }

    @Override
    public String toString() {
        return "CouchRestDiscoveredCrudRepository{" +
                "crudRepository=" + crudRepository +
                "} " + super.toString();
    }

}
