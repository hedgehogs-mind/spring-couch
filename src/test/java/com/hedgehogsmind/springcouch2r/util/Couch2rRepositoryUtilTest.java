package com.hedgehogsmind.springcouch2r.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public class Couch2rRepositoryUtilTest {

    public interface MySimpleCrudRepo extends CrudRepository<Date, Long> {}

    @Test
    public void testGetEntityClassOfSimpleCrudRepo() {

        final Class<?> entityClass = Couch2rRepositoryUtil.getEntityClassOfRepositoryClass(
                MySimpleCrudRepo.class
        );

        Assertions.assertEquals(Date.class, entityClass);
    }

}
