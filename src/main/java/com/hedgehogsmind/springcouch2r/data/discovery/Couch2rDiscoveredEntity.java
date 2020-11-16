package com.hedgehogsmind.springcouch2r.data.discovery;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Convenience class. This class does not add any extra information. It enables us
 * just to directly see, that the unit represents a tagged entity class.
 */
public class Couch2rDiscoveredEntity extends Couch2rDiscoveredUnit {

    public Couch2rDiscoveredEntity(Couch2r tagAnnotation, List<Annotation> couch2rModifierAnnotations, Class<?> entityClass, EntityType entityType) {
        super(tagAnnotation, couch2rModifierAnnotations, entityClass, entityType);
    }
}
