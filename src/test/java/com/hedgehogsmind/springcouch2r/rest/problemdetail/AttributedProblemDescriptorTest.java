package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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

    @Test
    public void testToResponseEntity() {
        final AttributedProblemDescriptor attributed = new ProblemDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                203
        ).withAttributes().addAttribute("k1", "v1").addAttribute("k2", "v2");

        final ResponseEntity re = attributed.toResponseEntity();

        Assertions.assertEquals(attributed, re.getBody());
        Assertions.assertEquals(attributed.getProblemDescriptor().getStatus(), re.getStatusCode().value());
        Assertions.assertEquals(
                ProblemDetail.MEDIA_TYPE.toString(),
                re.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)
        );
    }

}
