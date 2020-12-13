package com.hedgehogsmind.springcouch2r.beans;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation to import beans necessary for Couch2r to work properly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        Couch2rCore.class, // handles entity and repo scanning
        Couch2rHandlerMapping.class, // handles mapping requests to Couch2rMapping instances from Couch2rCore
        Couch2rHandlerAdapter.class // handles execution of Couch2rMapping instances
})
public @interface EnableCouch2r {

}
