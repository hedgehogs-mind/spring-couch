package com.hedgehogsmind.springcouchrest.beans.core.core1;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.beans.exceptions.EntityAlreadyManagedByRepositoryException;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.DummyTestCouchRestConfiguration;
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
public class CouchRestCoreEntityAlsoMappedByRepoTest {

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class}, scanBasePackageClasses = {})
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class Config {
        @Bean
        public DummyTestCouchRestConfiguration config() {
            return new DummyTestCouchRestConfiguration();
        }
    }

    @Entity
    @CouchRest
    public static class TestEntity1 {
        @Id
        @GeneratedValue
        public long id;
    }

    @Repository
    @CouchRest
    public interface Repo1 extends CrudRepository<TestEntity1, Long> {
    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testExceptionThrownIfEntityAlreadyManagedByCouchRestRepo() {
        Assertions.assertThrows(
                EntityAlreadyManagedByRepositoryException.class,
                () -> {
                    final CouchRestCore couchRestCore = new CouchRestCore(applicationContext, entityManager, Optional.empty());
                    couchRestCore.setup();
                }
        );
    }
}
