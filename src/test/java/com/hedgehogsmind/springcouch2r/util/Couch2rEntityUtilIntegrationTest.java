package com.hedgehogsmind.springcouch2r.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.*;

@DataJpaTest
public class Couch2rEntityUtilIntegrationTest {

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
                Couch2rEntityUtil.getEntityTypeByEntityClass(NonEntityClass.class, entityManager).isPresent()
        );

        Assertions.assertTrue(
                Couch2rEntityUtil.getEntityTypeByEntityClass(DummyEntity.class, entityManager).isPresent()
        );
    }

    @Test
    public void testGetRequiredEntityType() {
        Assertions.assertNotNull(
                Couch2rEntityUtil.getRequiredEntityTypeByEntityClass(DummyEntity.class, entityManager)
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Couch2rEntityUtil.getRequiredEntityTypeByEntityClass(NonEntityClass.class, entityManager)
        );
    }

}
