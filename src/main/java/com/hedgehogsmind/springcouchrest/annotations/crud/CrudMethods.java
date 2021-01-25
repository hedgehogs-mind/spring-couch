package com.hedgehogsmind.springcouchrest.annotations.crud;

import com.hedgehogsmind.springcouchrest.annotations.CouchRestModifierAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     This annotation enables you to explicitly disable CRUD methods of an
 *     entity or repository.
 * </p>
 *
 * <p>
 *     To enable a certain method, just overwrite the corresponding attribute of this
 *     annotation with false.
 * </p>
 *
 * <p>
 *     By default, this annotation "enables" all CRUD methods. This is the same behaviour like
 *     when you simply put {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest} on an
 *     entity or repository. <b>So putting this annotation on an entity or repository without
 *     overwriting the default attributes has no further impact on the CRUD methods!</b>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@CouchRestModifierAnnotation
public @interface CrudMethods {

    /**
     * This flag determines whether the HTTP GET method shall be enabled for CRUD resources (entities/resources).
     *
     * @return Enable GET CRUD method?
     */
    boolean get() default true;

    /**
     * This flag determines whether the HTTP POST method shall be enabled for CRUD resources (entities/resources).
     * This has an impact on the save and update actions.
     *
     * @return Enable POST (save/update) CRUD method?
     */
    boolean saveUpdate() default true;

    /**
     * This flag determines whether the HTTP DELETE method shall be enabled for CRUD resources (entities/resources).
     *
     * @return Enable DELETE CRUD method?
     */
    boolean delete() default true;

}
