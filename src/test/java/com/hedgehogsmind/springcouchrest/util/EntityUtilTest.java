package com.hedgehogsmind.springcouchrest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityUtilTest {

    public static class DummyEntityWithALotOfWords {}

    public static class A {}

    public static class b {}

    @Test
    public void testSnakeCaseEntityName() {
        Assertions.assertEquals(
                "dummyEntityWithALotOfWords",
                EntityUtil.getEntityClassNameSnakeCase(DummyEntityWithALotOfWords.class)
        );

        Assertions.assertEquals(
                "a",
                EntityUtil.getEntityClassNameSnakeCase(A.class)
        );

        Assertions.assertEquals(
                "b",
                EntityUtil.getEntityClassNameSnakeCase(b.class)
        );
    }

}
