package com.hedgehogsmind.springcouchrest.annotations.security;

import com.hedgehogsmind.springcouchrest.annotations.CouchRestModifierAnnotation;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation can be annotated to an entity or repository annotated
 * with {@link com.hedgehogsmind.springcouchrest.annotations.CouchRest}.
 * </p>
 *
 * <p>
 *     All members of this annotation declare security rules for different crud methods.
 *     <b>In case any of the rules is empty, the rule {@link CouchRestConfiguration#getBaseSecurityRule()}
 *     will be used.</b> We explain the rules in more detail:
 *
 *     <ul>
 *         <li>{@link #read()}: Rule protects a 'GET all' or 'GET one by id' request.</li>
 *         <li>{@link #saveUpdate()}: Rule protects a 'POST new' or 'POST update by id' request.</li>
 *         <li>{@link #delete()}: Rule protects a 'DELETE one by id' request.</li>
 *     </ul>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@CouchRestModifierAnnotation
public @interface CrudSecurity {

    /**
     * Restricts GET requests.
     *
     * @return SpringEL expression or empty. If empty, global base rule will be used.
     */
    String read() default "";

    /**
     * Restricts POST requests.
     *
     * @return SpringEL expression or empty. If empty, global base rule will be used.
     */
    String saveUpdate() default "";

    /**
     * Restricts DELETE requests.
     *
     * @return SpringEL expression or empty. If empty, global base rule will be used.
     */
    String delete() default "";

}
