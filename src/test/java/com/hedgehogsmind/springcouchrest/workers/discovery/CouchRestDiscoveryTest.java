package com.hedgehogsmind.springcouchrest.workers.discovery;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@DataJpaTest
public class CouchRestDiscoveryTest {

    @SpringBootApplication
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class TestApp {

    }

    @Configuration
    @ComponentScan(basePackageClasses = CouchRestDiscoveryTest.class)
    public static class TestConfig {

    }

    @Entity
    public static class RepoEntity1 {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Entity
    public static class RepoEntity2 {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Entity
    public static class RepoEntity3 {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @CouchRest
    @Entity
    public static class StandaloneEntity1WithCouchRest {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Entity
    public static class StandaloneEntity2WithoutCouchRest {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Repository
    @CouchRest
    public static interface Repo1WithCouchRest extends CrudRepository<RepoEntity1, Long> {}

    @Repository
    @CouchRest
    public static interface Repo2WithCouchRest extends CrudRepository<RepoEntity2, Long> {}

    @Repository
    public static interface Repo3WithoutCouchRest extends CrudRepository<RepoEntity3, Long> {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testDiscovery() {
        final CouchRestDiscovery discovery = new CouchRestDiscovery(applicationContext, entityManager);

        Assertions.assertEquals(
                2,
                discovery.getDiscoveredCrudRepositories().size(),
                "Expected Repo1 and Repo2"
        );

        final Set<Object> repoSources = discovery.getDiscoveredCrudRepositories()
                .stream()
                .map(d -> d.getTagAnnotationSource())
                .collect(Collectors.toSet());

        repoSources.remove(Repo1WithCouchRest.class);
        repoSources.remove(Repo2WithCouchRest.class);

        Assertions.assertTrue(
                repoSources.isEmpty(),
                "Repos discovered, but not expected: "+
                        repoSources.stream().map(Object::toString).collect(Collectors.joining(", "))
        );

        Assertions.assertEquals(
                1,
                discovery.getDiscoveredEntities().size(),
                "Expected only StandaloneEntity1"
        );

        final Set<Class> entityClasses = discovery.getDiscoveredEntities()
                .stream()
                .map(d -> d.getEntityClass())
                .collect(Collectors.toSet());

        entityClasses.remove(StandaloneEntity1WithCouchRest.class);

        Assertions.assertTrue(
                entityClasses.isEmpty(),
                "Entities discovered, but not expected: "+
                        entityClasses.stream().map(Objects::toString).collect(Collectors.joining(", "))
        );
    }

}
