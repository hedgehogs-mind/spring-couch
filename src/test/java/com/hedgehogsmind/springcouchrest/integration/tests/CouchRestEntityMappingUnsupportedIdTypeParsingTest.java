package com.hedgehogsmind.springcouchrest.integration.tests;

import com.hedgehogsmind.springcouchrest.integration.CouchRestIntegrationTestBase;
import com.hedgehogsmind.springcouchrest.integration.env.crud.EntityWithUnhandledIdType;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CouchRestEntityMappingUnsupportedIdTypeParsingTest
        extends CouchRestIntegrationTestBase {

    @BeforeAll
    public void assertState() {
        assertEntityMappingExists(EntityWithUnhandledIdType.class);
    }

    @Test
    void testGetOne() {
        final JSONObject response = getWithJsonObjectResponse(getBasePath()+"entityWithUnhandledIdType/1");

        assertProblemDetailGiven(
                CouchRestProblems.ID_TYPE_PARSING_NOT_SUPPORTED,
                response
        );
    }
}
