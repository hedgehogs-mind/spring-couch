package com.hedgehogsmind.springcouch2r.annotations;

import com.hedgehogsmind.springcouch2r.workers.discovery.Couch2rDiscovery;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Every Couch2r annotation, except {@link Couch2r}, must be annotated with
 * this annotation in order to be found by {@link Couch2rDiscovery}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Couch2rModifierAnnotation {
}
