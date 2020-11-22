package com.hedgehogsmind.springcouch2r.beans.core2;

import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rNoConfigurationFoundException;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
public class Couch2rCoreNoConfigurationTest {

    @SpringBootApplication(exclude = {Couch2rCore.class, Couch2rConfiguration.class})
    public static class Config {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testNoCouch2rConfigurationFoundExceptionThrown() {
        Assertions.assertThrows(
                Couch2rNoConfigurationFoundException.class,
                () -> {
                    final Couch2rCore couch2rCore = new Couch2rCore(applicationContext, entityManager, Optional.empty());
                    couch2rCore.setup();
                }
        );
    }

}
