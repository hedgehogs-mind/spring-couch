package com.hedgehogsmind.springcouch2r.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Couch2r {

    /**
     * Name under which the entity shall be handled (resource name). If empty,
     * {@link com.hedgehogsmind.springcouch2r.util.Couch2rEntityUtil#getEntityClassNameSnakeCase(Class)}
     * is used for resources names.
     *
     * @return Resource name.
     */
    String resourceName() default "";

}
