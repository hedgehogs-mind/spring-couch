package com.hedgehogsmind.springcouchrest.beans.core.core2;

import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.beans.exceptions.NoConfigurationFoundException;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
public class CouchRestCoreNoConfigurationTest {

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class})
    public static class Config {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testNoCouchRestConfigurationFoundExceptionThrown() {
        Assertions.assertThrows(
                NoConfigurationFoundException.class,
                () -> {
                    final CouchRestCore couchRestCore = new CouchRestCore(applicationContext, entityManager, Optional.empty());
                    couchRestCore.setup();
                }
        );
    }

}
