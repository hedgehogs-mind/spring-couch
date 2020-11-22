package com.hedgehogsmind.springcouch2r.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Couch2rEntityUtilTest {

    public static class DummyEntityWithALotOfWords {}

    public static class A {}

    public static class b {}

    @Test
    public void testSnakeCaseEntityName() {
        Assertions.assertEquals(
                "dummyEntityWithALotOfWords",
                Couch2rEntityUtil.getEntityClassNameSnakeCase(DummyEntityWithALotOfWords.class)
        );

        Assertions.assertEquals(
                "a",
                Couch2rEntityUtil.getEntityClassNameSnakeCase(A.class)
        );

        Assertions.assertEquals(
                "b",
                Couch2rEntityUtil.getEntityClassNameSnakeCase(b.class)
        );
    }

}
