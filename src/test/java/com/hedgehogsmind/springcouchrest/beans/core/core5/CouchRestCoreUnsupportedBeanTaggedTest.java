package com.hedgehogsmind.springcouchrest.beans.core.core5;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.DummyTestCouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.workers.discovery.exceptions.UnsupportedBeanTypeTaggedException;
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
public class CouchRestCoreUnsupportedBeanTaggedTest {

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class})
    public static class Config {
        @Bean
        public DummyTestCouchRestConfiguration config() {
            return new DummyTestCouchRestConfiguration();
        }

        @Bean
        public SomeService someService() {
            return new SomeService();
        }
    }

    @CouchRest
    public static class SomeService {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testExceptionThrownIfEntityAlreadyManagedByCouchRestRepo() {
        Assertions.assertThrows(
                UnsupportedBeanTypeTaggedException.class,
                () -> {
                    final CouchRestCore couchRestCore = new CouchRestCore(applicationContext, entityManager, Optional.empty());
                    couchRestCore.setup();
                }
        );
    }

}
