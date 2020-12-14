package com.hedgehogsmind.springcouch2r.workers.mapping.integration.tests;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.workers.mapping.integration.Couch2rIntegrationTestBase;
import com.hedgehogsmind.springcouch2r.workers.mapping.integration.env.EntityWithUnhandledIdType;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Couch2rEntityMappingUnsupportedIdTypeParsingTest extends Couch2rIntegrationTestBase {

    @BeforeAll
    public void assertState() {
        assertMappingExists(EntityWithUnhandledIdType.class);
    }

    @Test
    void testGetOne() {
        perform(mockRequest("GET", getBasePath()+"entityWithUnhandledIdType/1"));
        final JSONObject response = getResponseJson();

        Assertions.assertTrue(response.has("type"));
        Assertions.assertEquals(Couch2rProblems.ID_TYPE_PARSING_NOT_SUPPORTED.getType().toString(), response.getString("type"));
    }
}
