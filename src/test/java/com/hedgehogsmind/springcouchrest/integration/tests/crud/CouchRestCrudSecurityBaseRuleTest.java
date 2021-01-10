package com.hedgehogsmind.springcouchrest.integration.tests.crud;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * With this test class we want to test the impact of
 * {@link CouchRestConfiguration#getBaseSecurityRule()} on the generated crud endpoints.
 */
public class CouchRestCrudSecurityBaseRuleTest extends CouchRestCrudIntegrationTestBase {

    private static final String AUTHORITY = "API";

    @Override
    protected String getBaseSecurityRule() {
        return "isAuthenticated() && hasAuthority('"+AUTHORITY+"')";
    }

    @Override
    protected String getDefaultEndpointSecurityRule() {
        return "permitAll()";
    }

    @BeforeEach
    void cleanAuthentication() {
        removeAuthentication();
    }

    @Test
    void testGetAllWithoutAuth() {
        final JSONObject res = getWithJsonObjectResponse(getNoteBasePath());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetAllWithAuthButMissingAuthority() {
        login();

        final JSONObject res = getWithJsonObjectResponse(getNoteBasePath());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetAllWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(AUTHORITY);
        login();

        get(getNoteBasePath());
        assertStatusCode(200);
    }




    @Test
    void testGetOneWithoutAuth() {
    }

    @Test
    void testGetOneWithAuthButMissingAuthority() {
    }

    @Test
    void testGetOneWithAuthAndCorrectAuthority() {
    }



    @Test
    void testPostNewAllWithoutAuth() {
    }

    @Test
    void testPostNewWithAuthButMissingAuthority() {
    }

    @Test
    void testPostNewWithAuthAndCorrectAuthority() {
    }



    @Test
    void testPostUpdateWithoutAuth() {
    }

    @Test
    void testPostUpdateWithAuthButMissingAuthority() {
    }

    @Test
    void testPostUpdateWithAuthAndCorrectAuthority() {
    }



    @Test
    void testDeleteWithoutAuth() {
    }

    @Test
    void testDeleteWithAuthButMissingAuthority() {
    }

    @Test
    void testDeleteWithAuthAndCorrectAuthority() {
    }
}
