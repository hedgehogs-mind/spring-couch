package com.hedgehogsmind.springcouch2r.data.discovery;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Represents a {@link CrudRepository} which has been tagged
 * with {@link com.hedgehogsmind.springcouch2r.annotations.Couch2r}.
 */
public class Couch2rDiscoveredCrudRepository extends Couch2rDiscoveredUnit {

    private final CrudRepository crudRepository;

    /**
     * Stores values.
     * @param tagAnnotation See super.
     * @param tagAnnotationSource See super.
     * @param couch2rModifierAnnotations See super.
     * @param entityClass See super.
     * @param entityType See super.
     * @param crudRepository Discovered CrudRepository.
     */
    public Couch2rDiscoveredCrudRepository(Couch2r tagAnnotation, Object tagAnnotationSource, List<Annotation> couch2rModifierAnnotations, Class<?> entityClass, EntityType entityType, CrudRepository crudRepository) {
        super(tagAnnotation, tagAnnotationSource, couch2rModifierAnnotations, entityClass, entityType);
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
        Couch2rDiscoveredCrudRepository that = (Couch2rDiscoveredCrudRepository) o;
        return Objects.equals(crudRepository, that.crudRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), crudRepository);
    }

    @Override
    public String toString() {
        return "Couch2rDiscoveredCrudRepository{" +
                "crudRepository=" + crudRepository +
                "} " + super.toString();
    }

}
