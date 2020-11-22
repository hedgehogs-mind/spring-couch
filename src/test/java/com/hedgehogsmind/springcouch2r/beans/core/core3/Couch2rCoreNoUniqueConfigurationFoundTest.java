package com.hedgehogsmind.springcouch2r.beans.core.core3;

import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rNoUniqueConfigurationFoundException;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.configuration.DummyTestCouch2rConfiguration;
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
public class Couch2rCoreNoUniqueConfigurationFoundTest {

    public static class Couch2rTestConfig1 extends DummyTestCouch2rConfiguration {}

    public static class Couch2rTestConfig2 extends DummyTestCouch2rConfiguration {}

    @SpringBootApplication(exclude = {Couch2rCore.class, Couch2rConfiguration.class})
    @Configuration
    public static class Config {

        @Bean
        public static Couch2rTestConfig1 testConfig1() {
            return new Couch2rTestConfig1();
        }

        @Bean
        public static Couch2rTestConfig2 testConfig2() {
            return new Couch2rTestConfig2();
        }

    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testNoUniqueCouch2rConfigurationExceptionThrown() {
        Assertions.assertThrows(
                Couch2rNoUniqueConfigurationFoundException.class,
                () -> {
                    final Couch2rCore couch2rCore = new Couch2rCore(
                            applicationContext,
                            entityManager,
                            Optional.empty()
                    );

                    couch2rCore.setup();
                }
        );
    }

}
