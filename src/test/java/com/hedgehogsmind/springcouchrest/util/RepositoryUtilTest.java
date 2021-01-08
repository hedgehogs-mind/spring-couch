package com.hedgehogsmind.springcouchrest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public class RepositoryUtilTest {

    public interface MySimpleCrudRepo extends CrudRepository<Date, Long> {}

    @Test
    public void testGetEntityClassOfSimpleCrudRepo() {

        final Class<?> entityClass = RepositoryUtil.getEntityClassOfRepositoryClass(
                MySimpleCrudRepo.class
        );

        Assertions.assertEquals(Date.class, entityClass);
    }

}
