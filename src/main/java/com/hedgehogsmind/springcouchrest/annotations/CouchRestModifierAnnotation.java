package com.hedgehogsmind.springcouchrest.annotations;

import com.hedgehogsmind.springcouchrest.workers.discovery.CouchRestDiscovery;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Every CouchRest annotation, except {@link CouchRest}, which adds metadata to a mapped element,
 * must be annotated with this annotation in order to be found by {@link CouchRestDiscovery} and
 * processed later on.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CouchRestModifierAnnotation {
}
