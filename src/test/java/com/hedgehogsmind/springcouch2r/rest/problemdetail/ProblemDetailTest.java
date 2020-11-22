package com.hedgehogsmind.springcouch2r.rest.problemdetail;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Date;

public class ProblemDetailTest {

    private ProblemDetail createBugsBunnyProblem() {
        return ProblemDetail.byUrn(
                "123abc",
                "Bugs",
                "Bunny",
                201
        );
    }

    @Test
    public void testByUrn() {
        final ProblemDetail detail = createBugsBunnyProblem();

        Assertions.assertEquals(URI.create("urn:problem-type:123abc"), detail.getType());
        Assertions.assertEquals("Bugs", detail.getTitle());
        Assertions.assertEquals("Bunny", detail.getDetail());
        Assertions.assertEquals(201, detail.getStatus());
        Assertions.assertTrue(detail.getInstance().toString().startsWith("urn:uuid:"));
    }

    @Test
    public void testAttributes() {
        final ProblemDetail detail = createBugsBunnyProblem();

        Assertions.assertNotNull(detail.getFurtherAttributes());
        Assertions.assertTrue(detail.getFurtherAttributes().isEmpty());

        final Date d = new Date();
        detail.addAttribute("snakeCase", d);

        Assertions.assertTrue(detail.getFurtherAttributes().containsKey("snakeCase"));
        Assertions.assertSame(d, detail.getFurtherAttributes().get("snakeCase"));
    }

    @Test
    public void testSerializationWithoutAttributes() throws JSONException {
        final ProblemDetail detail = createBugsBunnyProblem();

        final String detailJson = detail.serializeAsJson();
        final JSONObject parsed = new JSONObject(detailJson);

        Assertions.assertEquals(5, parsed.length());
        Assertions.assertTrue(parsed.has("type"));
        Assertions.assertTrue(parsed.has("title"));
        Assertions.assertTrue(parsed.has("detail"));
        Assertions.assertTrue(parsed.has("status"));
        Assertions.assertTrue(parsed.has("instance"));

        Assertions.assertEquals("urn:problem-type:123abc", parsed.getString("type"));
        Assertions.assertEquals("Bugs", parsed.get("title"));
        Assertions.assertEquals("Bunny", parsed.get("detail"));
        Assertions.assertEquals(201, parsed.get("status"));
        Assertions.assertTrue(parsed.getString("instance").startsWith("urn:uuid:"));
    }

    @Test
    public void testSerializationFurtherAttributes() throws JSONException {
        final ProblemDetail detail = createBugsBunnyProblem();

        detail.addAttribute("f1", "add. 1");
        detail.addAttribute("f2", "add. 2");

        final String detailJson = detail.serializeAsJson();
        final JSONObject parsed = new JSONObject(detailJson);

        Assertions.assertEquals(7, parsed.length());
        Assertions.assertTrue(parsed.has("f1"));
        Assertions.assertTrue(parsed.has("f2"));

        Assertions.assertEquals("add. 1", parsed.get("f1"));
        Assertions.assertEquals("add. 2", parsed.get("f2"));
    }

    private void testAttributeNameForbidden(final String attributeName) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createBugsBunnyProblem().addAttribute(attributeName, "test"),
                "Name '"+attributeName+"' should not be used for further attributes, but was applicable."
        );
    }

    @Test
    public void testForbiddenAttributeNames() {
        testAttributeNameForbidden("status");
        testAttributeNameForbidden("title");
        testAttributeNameForbidden("detail");
        testAttributeNameForbidden("status");
        testAttributeNameForbidden("instance");
    }

    @Test
    public void checkMediaType() {
        Assertions.assertEquals(
                "application/problem+json",
                ProblemDetail.MEDIA_TYPE.toString()
        );
    }

}
