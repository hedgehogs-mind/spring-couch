package com.hedgehogsmind.springcouchrest.annotations;

import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.beans.CouchRestHandlerAdapter;
import com.hedgehogsmind.springcouchrest.beans.CouchRestHandlerMapping;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation to import beans necessary for CouchRest to work properly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        CouchRestCore.class, // handles entity and repo scanning
        CouchRestHandlerMapping.class, // handles mapping requests to MappingHandler instances from CouchRestCore
        CouchRestHandlerAdapter.class // handles execution of CouchRest instances
})
public @interface EnableCouchRest {

}
