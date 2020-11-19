package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class ProblemDetailTest {

    @Test
    public void testByUrn() {
        final ProblemDetail detail = ProblemDetail.byUrn(
                "123abc",
                "Bugs",
                "Bunny",
                201
        );

        Assertions.assertEquals(URI.create("urn:problem-type:123abc"), detail.getType());
        Assertions.assertEquals("Bugs", detail.getTitle());
        Assertions.assertEquals("Bunny", detail.getDetail());
        Assertions.assertEquals(201, detail.getStatus());
        Assertions.assertTrue(detail.getInstance().toString().startsWith("urn:uuid:"));
    }

}
