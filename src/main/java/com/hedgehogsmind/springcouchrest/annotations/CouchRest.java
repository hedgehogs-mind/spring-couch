package com.hedgehogsmind.springcouchrest.annotations;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
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

    /**
     * This flag determines, if CouchRest shall check the base security rule
     * defined via {@link CouchRestConfiguration#getBaseSecurityRule()} before checking
     * the security rules on endpoint level.
     *
     * @return True if base security rule shall be checked, fasle if it shall be ignored.
     */
    boolean checkBaseSecurityRule() default true;

}
