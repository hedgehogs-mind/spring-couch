package com.hedgehogsmind.springcouchrest.beans.core.core3;

import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.beans.exceptions.NoUniqueConfigurationFoundException;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.DummyTestCouchRestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
public class CouchRestCoreNoUniqueConfigurationFoundTest {

    public static class CouchRestTestConfig1
            extends DummyTestCouchRestConfiguration {}

    public static class CouchRestTestConfig2 extends DummyTestCouchRestConfiguration {}

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class})
    @Configuration
    public static class Config {

        @Bean
        public static CouchRestTestConfig1 testConfig1() {
            return new CouchRestTestConfig1();
        }

        @Bean
        public static CouchRestTestConfig2 testConfig2() {
            return new CouchRestTestConfig2();
        }

    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testNoUniqueCouchRestConfigurationExceptionThrown() {
        Assertions.assertThrows(
                NoUniqueConfigurationFoundException.class,
                () -> {
                    final CouchRestCore couchRestCore = new CouchRestCore(
                            applicationContext,
                            entityManager,
                            Optional.empty()
                    );

                    couchRestCore.setup();
                }
        );
    }

}
