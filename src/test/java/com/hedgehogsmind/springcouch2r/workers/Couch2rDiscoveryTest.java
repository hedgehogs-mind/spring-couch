package com.hedgehogsmind.springcouch2r.workers;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@DataJpaTest
public class Couch2rDiscoveryTest {

    @SpringBootApplication
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class TestApp {

    }

    @Configuration
    @ComponentScan(basePackageClasses = Couch2rDiscoveryTest.class)
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

    @Couch2r
    @Entity
    public static class StandaloneEntity1WithCouch2r {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Entity
    public static class StandaloneEntity2WithoutCouch2r {
        @Id
        @GeneratedValue
        public long id;

        @Column
        public String text;
    }

    @Repository
    @Couch2r
    public static interface Repo1WithCouch2r extends CrudRepository<RepoEntity1, Long> {}

    @Repository
    @Couch2r
    public static interface Repo2WithCouch2r extends CrudRepository<RepoEntity2, Long> {}

    @Repository
    public static interface Repo3WithoutCouch2r extends CrudRepository<RepoEntity3, Long> {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testDiscovery() {
        final Couch2rDiscovery discovery = new Couch2rDiscovery(applicationContext, entityManager);

        Assertions.assertEquals(
                2,
                discovery.getDiscoveredCrudRepositories().size(),
                "Expected Repo1 and Repo2"
        );

        final Set<Object> repoSources = discovery.getDiscoveredCrudRepositories()
                .stream()
                .map(d -> d.getTagAnnotationSource())
                .collect(Collectors.toSet());

        repoSources.remove(Repo1WithCouch2r.class);
        repoSources.remove(Repo2WithCouch2r.class);

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

        entityClasses.remove(StandaloneEntity1WithCouch2r.class);

        Assertions.assertTrue(
                entityClasses.isEmpty(),
                "Entities discovered, but not expected: "+
                        entityClasses.stream().map(Objects::toString).collect(Collectors.joining(", "))
        );
    }

}
