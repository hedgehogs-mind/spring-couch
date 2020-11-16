package com.hedgehogsmind.springcouch2r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Every Couch2r annotation, except {@link Couch2r}, must be annotated with
 * this annotation in order to be found by {@link com.hedgehogsmind.springcouch2r.workers.Couch2rDiscovery}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Couch2rModifierAnnotation {
}
