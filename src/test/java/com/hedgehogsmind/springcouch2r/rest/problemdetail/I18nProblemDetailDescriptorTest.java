package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Locale;

public class I18nProblemDetailDescriptorTest {

    @Test
    void testResourceBundleAccessible() {
        I18nProblemDetailDescriptor.getResourceBundle(Locale.ENGLISH).getString("test.test.test");
    }

    @Test
    void testGettersWithPlainConstructor() {
        final URI type = URI.create("urn:problem-type:test");

        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
            type,
            "key.one",
            "key.two",
            200
        );

        Assertions.assertEquals(type, descriptor.getType());
        Assertions.assertEquals("key.one", descriptor.getTitleKey());
        Assertions.assertEquals("key.two", descriptor.getDetailKey());
        Assertions.assertEquals(200, descriptor.getStatus());
    }

    @Test
    void testUrnConstructor() {
        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "stringType",
                "key.one",
                "key.two",
                200
        );

        Assertions.assertEquals(URI.create("urn:problem-type:stringType"), descriptor.getType());
    }

    @Test
    void testEqualsAndHashCode() {
        final URI type1 = URI.create("urn:problem-type:test");
        final URI type2 = URI.create("urn:problem-type:test");

        final I18nProblemDetailDescriptor descriptor1 = new I18nProblemDetailDescriptor(
                type1,
                "key.one",
                "key.two",
                200
        );

        final I18nProblemDetailDescriptor descriptor2 = new I18nProblemDetailDescriptor(
                type2,
                "key.one",
                "key.two",
                200
        );

        Assertions.assertEquals(descriptor1.hashCode(), descriptor2.hashCode());
        Assertions.assertTrue(descriptor1.equals(descriptor2));
    }

    @Test
    void testToProblemDetail() {
        final String resolvedMessage = I18nProblemDetailDescriptor
                .getResourceBundle(Locale.ENGLISH)
                .getString("test.test.test");

        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "stringType",
                "test.test.test",
                "test.test.test",
                200
        );

        final ProblemDetail problemDetail = descriptor.toProblemDetail(Locale.ENGLISH);

        Assertions.assertEquals(URI.create("urn:problem-type:stringType"), problemDetail.getType());
        Assertions.assertEquals(resolvedMessage, problemDetail.getTitle());
        Assertions.assertEquals(resolvedMessage, problemDetail.getDetail());
        Assertions.assertEquals(200, problemDetail.getStatus());
        Assertions.assertTrue(problemDetail.getInstance().toString().contains("urn:uuid:"));
    }

    @Test
    void testAttributed() {
        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "stringType",
                "test.test.test",
                "test.test.test",
                200
        );

        Assertions.assertEquals(
                new AttributedI18nProblemDetailDescriptor(descriptor),
                descriptor.withAttributes()
        );
    }
}
