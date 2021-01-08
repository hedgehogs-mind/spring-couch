package com.hedgehogsmind.springcouchrest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.*;

@DataJpaTest
public class EntityUtilIntegrationTest {

    @SpringBootApplication(scanBasePackageClasses = {})
    public static class App {

    }

    @Entity
    public static class DummyEntity {
        @Id
        @GeneratedValue
        public int id;

        @Column
        public String value;
    }

    public static class NonEntityClass {

    }

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testGetOptionalEntityType() {
        Assertions.assertFalse(
                EntityUtil.getEntityTypeByEntityClass(NonEntityClass.class, entityManager).isPresent()
        );

        Assertions.assertTrue(
                EntityUtil.getEntityTypeByEntityClass(DummyEntity.class, entityManager).isPresent()
        );
    }

    @Test
    public void testGetRequiredEntityType() {
        Assertions.assertNotNull(
                EntityUtil.getRequiredEntityTypeByEntityClass(DummyEntity.class, entityManager)
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> EntityUtil.getRequiredEntityTypeByEntityClass(NonEntityClass.class, entityManager)
        );
    }

}
