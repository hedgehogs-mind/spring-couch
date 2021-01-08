package com.hedgehogsmind.springcouchrest.data.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Convenience class. This class does not add any extra information. It enables us
 * just to directly see, that the unit represents a tagged entity class.
 */
public class DiscoveredEntity
        extends DiscoveredUnit {

    public DiscoveredEntity(CouchRest tagAnnotation, Object tagAnnotationSource, List<Annotation> couchRestModifierAnnotations, Class<?> entityClass, EntityType entityType) {
        super(tagAnnotation, tagAnnotationSource, couchRestModifierAnnotations, entityClass, entityType);
    }

}
