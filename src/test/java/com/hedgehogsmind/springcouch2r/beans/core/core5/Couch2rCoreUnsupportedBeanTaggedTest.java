package com.hedgehogsmind.springcouch2r.beans.core.core5;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.configuration.DummyTestCouch2rConfiguration;
import com.hedgehogsmind.springcouch2r.workers.discovery.exceptions.Couch2rUnsupportedBeanTypeTaggedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
public class Couch2rCoreUnsupportedBeanTaggedTest {

    @SpringBootApplication(exclude = {Couch2rCore.class, Couch2rConfiguration.class})
    public static class Config {
        @Bean
        public DummyTestCouch2rConfiguration config() {
            return new DummyTestCouch2rConfiguration();
        }

        @Bean
        public SomeService someService() {
            return new SomeService();
        }
    }

    @Couch2r
    public static class SomeService {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testExceptionThrownIfEntityAlreadyManagedByCouch2rRepo() {
        Assertions.assertThrows(
                Couch2rUnsupportedBeanTypeTaggedException.class,
                () -> {
                    final Couch2rCore couch2rCore = new Couch2rCore(applicationContext, entityManager, Optional.empty());
                    couch2rCore.setup();
                }
        );
    }

}
