package com.hedgehogsmind.springcouch2r.beans.core4;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rResourcePathClashException;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.configuration.DummyTestCouch2rConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

@DataJpaTest
public class Couch2rCorePathClashTest {

    @SpringBootApplication(exclude = {Couch2rCore.class, Couch2rConfiguration.class})
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class Config {
        @Bean
        public DummyTestCouch2rConfiguration config() {
            return new DummyTestCouch2rConfiguration();
        }
    }

    @Entity
    public static class TestEntity1 {
        @Id @GeneratedValue
        public long id;
    }

    @Repository
    @Couch2r(resourceName = "clashName")
    public interface Repo1 extends CrudRepository<TestEntity1, Long> {}

    @Entity
    @Couch2r(resourceName = "clashName")
    public static class TestEntity2 {
        @Id @GeneratedValue
        public long id;
    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testExceptionOnPathClash() {
        Assertions.assertThrows(
                Couch2rResourcePathClashException.class,
                () -> {
                    final Couch2rCore couch2rCore = new Couch2rCore(applicationContext, entityManager, Optional.empty());
                    couch2rCore.setup();
                }
        );
    }



}
