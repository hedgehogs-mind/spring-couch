package com.hedgehogsmind.springcouch2r.rest.problemdetail;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProblemDescriptorTest {

    @Test
    public void testEnglishResourceBundle() {
        final ResourceBundle rb = ProblemDescriptor.getResourceBundle(Locale.ENGLISH);
        Assertions.assertEquals(
                "Couch2r en",
                rb.getString("test.test.test")
        );
    }

    @Test
    public void testGermanResourceBundle() {
        final ResourceBundle rb = ProblemDescriptor.getResourceBundle(Locale.GERMAN);
        Assertions.assertEquals(
                "Couch2r de",
                rb.getString("test.test.test")
        );
    }

    @Test
    public void testFallbackEnglish() {
        final ResourceBundle rb = ProblemDescriptor.getResourceBundle(Locale.forLanguageTag("xy"));

        Assertions.assertEquals(
                "Couch2r en",
                rb.getString("test.test.test")
        );
    }

    @Test
    public void testDescriptorToProblemDetail() {
        final ProblemDescriptor descriptor = new ProblemDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                203
        );

        final ProblemDetail detail = descriptor.toProblemDetail(Locale.ENGLISH);
        Assertions.assertEquals(URI.create("urn:problem-type:testType"), detail.getType());
        Assertions.assertEquals("Couch2r en", detail.getTitle());
        Assertions.assertEquals("Couch2r en", detail.getDetail());
        Assertions.assertEquals(203, detail.getStatus());
    }

    @Test
    public void testToResponseEntity() {
        final ProblemDescriptor descriptor = new ProblemDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                203
        );

        final ResponseEntity re = descriptor.toResponseEntity();

        Assertions.assertEquals(descriptor, re.getBody());
        Assertions.assertEquals(descriptor.getStatus(), re.getStatusCode().value());
        Assertions.assertEquals(
                ProblemDetail.MEDIA_TYPE.toString(),
                re.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)
        );
    }

}
