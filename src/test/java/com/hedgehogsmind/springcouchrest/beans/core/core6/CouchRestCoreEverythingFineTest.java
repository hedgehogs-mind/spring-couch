package com.hedgehogsmind.springcouchrest.beans.core.core6;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.DummyTestCouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
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

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
public class CouchRestCoreEverythingFineTest {

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class}, scanBasePackageClasses = {})
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class Config {
        @Bean
        public DummyTestCouchRestConfiguration config() {
            return new DummyTestCouchRestConfiguration();
        }
    }

    @Entity
    public static class RepoEntity1 {
        @Id @GeneratedValue
        public long id;
        @Column
        public String value;
    }

    @Entity
    public static class RepoEntity2 {
        @Id @GeneratedValue
        public long id;
        @Column
        public String value;
    }

    @Entity
    @CouchRest
    public static class StandaloneEntity1 {
        @Id @GeneratedValue
        public long id;
        @Column
        public String value;
    }

    @Entity
    @CouchRest(resourceName = "standalone2")
    public static class StandaloneEntity2 {
        @Id @GeneratedValue
        public long id;
        @Column
        public String value;
    }

    @Repository
    @CouchRest
    public interface Repo1 extends CrudRepository<RepoEntity1, Long> {}

    @Repository
    @CouchRest(resourceName = "B2")
    public interface Repo2 extends CrudRepository<RepoEntity2, Long> {}

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testEverythingIsFine() {
        final CouchRestCore core = new CouchRestCore(
                applicationContext, entityManager, Optional.empty()
        );

        core.setup();

        Assertions.assertNotNull(core.getCouchRestConfiguration());
        Assertions.assertNotNull(core.getCouchRestObjectMapper());

        final CouchRestConfiguration config = core.getCouchRestConfiguration();
        final String prefix = config.getCouchRestBasePath();

        final Set<MappedResource> mappings = core.getMappedResources();
        final Optional<MappedEntityResource> repo1MappingResult = mappings.stream().filter(m -> m instanceof MappedEntityResource).map(m -> (MappedEntityResource)m).filter(m -> m.getEntityType().getJavaType() == RepoEntity1.class).findAny();
        final Optional<MappedEntityResource> repo2MappingResult = mappings.stream().filter(m -> m instanceof MappedEntityResource).map(m -> (MappedEntityResource)m).filter(m -> m.getEntityType().getJavaType() == RepoEntity2.class).findAny();
        final Optional<MappedEntityResource> standalone1MappingResult = mappings.stream().filter(m -> m instanceof MappedEntityResource).map(m -> (MappedEntityResource)m).filter(m -> m.getEntityType().getJavaType() == StandaloneEntity1.class).findAny();
        final Optional<MappedEntityResource> standalone2MappingResult = mappings.stream().filter(m -> m instanceof MappedEntityResource).map(m -> (MappedEntityResource)m).filter(m -> m.getEntityType().getJavaType() == StandaloneEntity2.class).findAny();

        Assertions.assertTrue(repo1MappingResult.isPresent(), "no mapping for RepoEntity1");
        Assertions.assertTrue(repo2MappingResult.isPresent(), "no mapping for RepoEntity2");
        Assertions.assertTrue(standalone1MappingResult.isPresent(), "no mapping for StandaloneEntity1");
        Assertions.assertTrue(standalone2MappingResult.isPresent(), "no mapping for StandaloneEntity2");

        final MappedEntityResource repo1Mapping = repo1MappingResult.get();
        final MappedEntityResource repo2Mapping = repo2MappingResult.get();
        final MappedEntityResource standalone1Mapping = standalone1MappingResult.get();
        final MappedEntityResource standalone2Mapping = standalone2MappingResult.get();

        // Check resource paths
        Assertions.assertEquals(prefix+"repoEntity1/", repo1Mapping.getResourcePathWithTrailingSlash());
        Assertions.assertEquals(prefix+"B2/", repo2Mapping.getResourcePathWithTrailingSlash());
        Assertions.assertEquals(prefix+"standaloneEntity1/", standalone1Mapping.getResourcePathWithTrailingSlash());
        Assertions.assertEquals(prefix+"standalone2/", standalone2Mapping.getResourcePathWithTrailingSlash());

        // Check repos are not null
        Assertions.assertNotNull(repo1Mapping.getRepository());
        Assertions.assertNotNull(repo2Mapping.getRepository());
        Assertions.assertNotNull(standalone1Mapping.getRepository());
        Assertions.assertNotNull(standalone2Mapping.getRepository());

        // Check entity types
        Assertions.assertEquals(RepoEntity1.class, repo1Mapping.getEntityType().getJavaType());
        Assertions.assertEquals(RepoEntity2.class, repo2Mapping.getEntityType().getJavaType());
        Assertions.assertEquals(StandaloneEntity1.class, standalone1Mapping.getEntityType().getJavaType());
        Assertions.assertEquals(StandaloneEntity2.class, standalone2Mapping.getEntityType().getJavaType());

        // Check that sources are correct
        Assertions.assertEquals(Repo1.class, repo1Mapping.getMappingSource().getTagAnnotationSource());
        Assertions.assertEquals(Repo2.class, repo2Mapping.getMappingSource().getTagAnnotationSource());
        Assertions.assertEquals(StandaloneEntity1.class, standalone1Mapping.getMappingSource().getTagAnnotationSource());
        Assertions.assertEquals(StandaloneEntity2.class, standalone2Mapping.getMappingSource().getTagAnnotationSource());
    }

}
