package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class AttributedProblemDescriptorTest {

    private final ProblemDescriptor DESCRIPTOR = new ProblemDescriptor(
            "abc", "test.test.test", "test.test.test", 302
    );

    @Test
    public void testToProblemDetail() {
        final AttributedProblemDescriptor attributed = DESCRIPTOR.withAttributes();

        final ProblemDetail fromDescriptor = DESCRIPTOR.toProblemDetail(Locale.ENGLISH);
        final ProblemDetail fromAttributedDescriptor = attributed.toProblemDetail(Locale.ENGLISH);

        Assertions.assertEquals(fromDescriptor.getType(), fromAttributedDescriptor.getType());
        Assertions.assertEquals(fromDescriptor.getTitle(), fromAttributedDescriptor.getTitle());
        Assertions.assertEquals(fromDescriptor.getDetail(), fromAttributedDescriptor.getDetail());
        Assertions.assertEquals(fromDescriptor.getStatus(), fromAttributedDescriptor.getStatus());
    }

    @Test
    public void testToProblemDetailWithThrowable() {
        final RuntimeException throwable = new RuntimeException("THIS IS a test");
        final AttributedProblemDescriptor attributed = DESCRIPTOR.withAttributes();

        final ProblemDetail fromDescriptor = DESCRIPTOR.toProblemDetail(Locale.ENGLISH, throwable);
        final ProblemDetail fromAttributedDescriptor = attributed.toProblemDetail(Locale.ENGLISH, throwable);

        Assertions.assertEquals(fromDescriptor.getType(), fromAttributedDescriptor.getType());
        Assertions.assertEquals(fromDescriptor.getTitle(), fromAttributedDescriptor.getTitle());
        Assertions.assertEquals(fromDescriptor.getDetail(), fromAttributedDescriptor.getDetail());
        Assertions.assertEquals(fromDescriptor.getStatus(), fromAttributedDescriptor.getStatus());
    }

    @Test
    public void testWithAttributes() {
        final ProblemDetail detail = DESCRIPTOR.withAttributes()
                .addAttribute("key1", "value1")
                .addAttribute("key2", "value2")
                .toProblemDetail(Locale.ENGLISH);

        Assertions.assertEquals(2, detail.getFurtherAttributes().size());
        Assertions.assertTrue(detail.getFurtherAttributes().containsKey("key1"));
        Assertions.assertTrue(detail.getFurtherAttributes().containsKey("key2"));
        Assertions.assertEquals("value1", detail.getFurtherAttributes().get("key1"));
        Assertions.assertEquals("value2", detail.getFurtherAttributes().get("key2"));
    }

    @Test
    public void testWithAttributesWhenThrowablePassed() {
        final ProblemDetail detail = DESCRIPTOR.withAttributes()
                .addAttribute("key1", "value1")
                .addAttribute("key2", "value2")
                .toProblemDetail(Locale.ENGLISH, new RuntimeException("Some error"));

        Assertions.assertEquals(2, detail.getFurtherAttributes().size());
        Assertions.assertTrue(detail.getFurtherAttributes().containsKey("key1"));
        Assertions.assertTrue(detail.getFurtherAttributes().containsKey("key2"));
        Assertions.assertEquals("value1", detail.getFurtherAttributes().get("key1"));
        Assertions.assertEquals("value2", detail.getFurtherAttributes().get("key2"));
    }

}
