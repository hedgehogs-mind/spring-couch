package com.hedgehogsmind.springcouchrest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathUtilTest {

    @Test
    public void testRemoveMultipleSlashes() {
        Assertions.assertEquals(
                "/hello/world/test",
                PathUtil.removeMultipleSlashes("/////hello//world///test")
        );
    }

    @Test
    public void testNormalizeTrailingSlash() {
        Assertions.assertEquals(
                "/hello/world/test/",
                PathUtil.normalizeWithTrailingSlash("///////hello////world/test")
        );

        Assertions.assertEquals(
                "/hello/world/test/",
                PathUtil.normalizeWithTrailingSlash("///////hello////world/test/")
        );

        Assertions.assertEquals(
                "",
                PathUtil.normalizeWithTrailingSlash("")
        );
    }

    @Test
    public void testNormalizeNoTrailingSlash() {
        Assertions.assertEquals(
                "/hello/world/test",
                PathUtil.normalizeWithoutTrailingSlash("///////hello////world/test////")
        );

        Assertions.assertEquals(
                "/hello/world/test",
                PathUtil.normalizeWithoutTrailingSlash("///////hello////world/test/")
        );

        Assertions.assertEquals(
                "/hello/world/test",
                PathUtil.normalizeWithoutTrailingSlash("///////hello////world/test")
        );
    }

}
