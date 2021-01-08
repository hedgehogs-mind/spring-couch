package com.hedgehogsmind.springcouchrest.annotations;

import com.hedgehogsmind.springcouchrest.util.EntityUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CouchRest {

    /**
     * Name under which the entity shall be handled (resource name). If empty,
     * {@link EntityUtil#getEntityClassNameSnakeCase(Class)}
     * is used for resources names.
     *
     * @return Resource name.
     */
    String resourceName() default "";

}
