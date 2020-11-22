package com.hedgehogsmind.springcouch2r.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Couch2rPathUtilTest {

    @Test
    public void testRemoveMultipleSlashes() {
        Assertions.assertEquals(
                "/hello/world/test",
                Couch2rPathUtil.removeMultipleSlashes("/////hello//world///test")
        );
    }

    @Test
    public void testNormalizeTrailingSlash() {
        Assertions.assertEquals(
                "/hello/world/test/",
                Couch2rPathUtil.normalizeWithTrailingSlash("///////hello////world/test")
        );

        Assertions.assertEquals(
                "/hello/world/test/",
                Couch2rPathUtil.normalizeWithTrailingSlash("///////hello////world/test/")
        );
    }

    @Test
    public void testNormalizeNoTrailingSlash() {
        Assertions.assertEquals(
                "/hello/world/test",
                Couch2rPathUtil.normalizeWithoutTrailingSlash("///////hello////world/test////")
        );

        Assertions.assertEquals(
                "/hello/world/test",
                Couch2rPathUtil.normalizeWithoutTrailingSlash("///////hello////world/test/")
        );

        Assertions.assertEquals(
                "/hello/world/test",
                Couch2rPathUtil.normalizeWithoutTrailingSlash("///////hello////world/test")
        );
    }

}
