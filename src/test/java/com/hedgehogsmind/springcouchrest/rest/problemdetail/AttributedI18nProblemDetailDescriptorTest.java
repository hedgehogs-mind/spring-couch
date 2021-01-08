package com.hedgehogsmind.springcouchrest.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Locale;

public class AttributedI18nProblemDetailDescriptorTest {

    private static final String RESOLVED_MESSAGE = I18nProblemDetailDescriptor
            .getResourceBundle(Locale.ENGLISH)
            .getString("test.test.test");

    private I18nProblemDetailDescriptor descriptor() {
        return new I18nProblemDetailDescriptor(
                "test",
                "test.test.test",
                "test.test.test",
                200
        );
    }

    @Test
    void testGetters() {
        final I18nProblemDetailDescriptor descriptor = descriptor();
        final AttributedI18nProblemDetailDescriptor attributed = descriptor.withAttributes();

        Assertions.assertEquals(descriptor, attributed.getDescriptor());
        Assertions.assertNotNull(attributed.getData());
    }

    @Test
    void testAddAttribute() {
        final I18nProblemDetailDescriptor descriptor = descriptor();
        final AttributedI18nProblemDetailDescriptor attributed = descriptor.withAttributes();

        attributed.addAttribute("key", "value");
        Assertions.assertEquals("value", attributed.getData().get("key"));
    }

    @Test
    void testEqualsAndHashCode() {
        final AttributedI18nProblemDetailDescriptor a1 = descriptor().withAttributes()
                .addAttribute("key", "value");

        final AttributedI18nProblemDetailDescriptor a2 = descriptor().withAttributes()
                .addAttribute("key", "value");

        Assertions.assertEquals(a1.hashCode(), a1.hashCode());
        Assertions.assertTrue(a1.equals(a2));
    }

    @Test
    void testToProblemDetail() {
        final ProblemDetail problemDetail = descriptor()
                .withAttributes()
                .addAttribute("key", "value")
                .toProblemDetail(Locale.ENGLISH);

        Assertions.assertEquals(URI.create("urn:problem-type:test"), problemDetail.getType());
        Assertions.assertEquals(RESOLVED_MESSAGE, problemDetail.getTitle());
        Assertions.assertEquals(RESOLVED_MESSAGE, problemDetail.getDetail());
        Assertions.assertEquals(200, problemDetail.getStatus());
        Assertions.assertTrue(problemDetail.getInstance().toString().contains("urn:uuid:"));

        Assertions.assertNotNull(problemDetail.getData());
        Assertions.assertEquals("value", problemDetail.getData().get("key"));
    }
}
