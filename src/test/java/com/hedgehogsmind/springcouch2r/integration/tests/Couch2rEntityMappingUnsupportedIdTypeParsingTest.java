package com.hedgehogsmind.springcouch2r.integration.tests;

import com.hedgehogsmind.springcouch2r.integration.Couch2rIntegrationTestBase;
import com.hedgehogsmind.springcouch2r.integration.env.EntityWithUnhandledIdType;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Couch2rEntityMappingUnsupportedIdTypeParsingTest extends Couch2rIntegrationTestBase {

    @BeforeAll
    public void assertState() {
        assertEntityMappingExists(EntityWithUnhandledIdType.class);
    }

    @Test
    void testGetOne() {
        final JSONObject response = getWithJsonObjectResponse(getBasePath()+"entityWithUnhandledIdType/1");

        assertProblemDetailGiven(
                Couch2rProblems.ID_TYPE_PARSING_NOT_SUPPORTED,
                response
        );
    }
}
