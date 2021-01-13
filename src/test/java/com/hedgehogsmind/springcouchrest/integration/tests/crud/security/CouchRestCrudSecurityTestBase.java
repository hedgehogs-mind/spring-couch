package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.integration.tests.crud.CouchRestCrudIntegrationTestBase;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * With this test class we want to test the impact of
 * {@link CouchRestConfiguration#getBaseSecurityRule()} on the generated crud endpoints.
 */
public abstract class CouchRestCrudSecurityTestBase
        extends CouchRestCrudIntegrationTestBase {
    
    protected abstract String getEntityBasePath();
    
    protected abstract Object getSomeIdOfExistingEntity();
    
    protected abstract String getNeededAuthority();

    @Override
    protected abstract String getBaseSecurityRule();

    @Override
    protected abstract String getDefaultEndpointSecurityRule();

    @BeforeEach
    void cleanAuthentication() {
        logout();
    }

    @Test
    void testGetAllWithoutAuth() {
        final JSONObject res = getWithJsonObjectResponse(getEntityBasePath());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetAllWithAuthButMissingAuthority() {
        login();

        final JSONObject res = getWithJsonObjectResponse(getEntityBasePath());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetAllWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(getNeededAuthority());
        login();

        get(getEntityBasePath());
        assertStatusCode(200);
    }

    @Test
    void testGetOneWithoutAuth() {
        final JSONObject res = getWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetOneWithAuthButMissingAuthority() {
        login();

        final JSONObject res = getWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity());

        assertProblemDetailGiven(
                CouchRestProblems.FORBIDDEN,
                res
        );
    }

    @Test
    void testGetOneWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(getNeededAuthority());
        login();

        get(getEntityBasePath()+getSomeIdOfExistingEntity());
        assertStatusCode(200);
    }

    @Test
    void testPostNewAllWithoutAuth() {
        final String newNoteJson = "{\"title\":\"New test note\",\"content\":\"Time: "+System.currentTimeMillis()+"\",\"rating\":4}";
        final JSONObject res = postWithJsonObjectResponse(getEntityBasePath(), newNoteJson);

        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testPostNewWithAuthButMissingAuthority() {
        login();

        final String newNoteJson = "{\"title\":\"New test note\",\"content\":\"Time: "+System.currentTimeMillis()+"\",\"rating\":4}";
        final JSONObject res = postWithJsonObjectResponse(getEntityBasePath(), newNoteJson);

        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testPostNewWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(getNeededAuthority());
        login();

        final String newNoteJson = "{\"title\":\"New test note\",\"content\":\"Time: "+System.currentTimeMillis()+"\",\"rating\":4}";
        post(getEntityBasePath(), newNoteJson);
        assertStatusCode(200);
    }

    @Test
    void testPostUpdateWithoutAuth() {
        final String updateJson = "{\"content\": \"New: "+System.currentTimeMillis()+"\"}";
        final JSONObject res = postWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity(), updateJson);
        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testPostUpdateWithAuthButMissingAuthority() {
        login();

        final String updateJson = "{\"content\": \"New: "+System.currentTimeMillis()+"\"}";
        final JSONObject res = postWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity(), updateJson);
        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testPostUpdateWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(getNeededAuthority());
        login();

        final String updateJson = "{\"content\": \"New: "+System.currentTimeMillis()+"\"}";
        postWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity(), updateJson);
        assertStatusCode(200);
    }

    @Test
    void testDeleteWithoutAuth() {
        final JSONObject res = deleteWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity());
        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testDeleteWithAuthButMissingAuthority() {
        login();

        final JSONObject res = deleteWithJsonObjectResponse(getEntityBasePath()+getSomeIdOfExistingEntity());
        assertProblemDetailGiven(CouchRestProblems.FORBIDDEN, res);
    }

    @Test
    void testDeleteWithAuthAndCorrectAuthority() {
        setAuthoritiesOfTestUser(getNeededAuthority());
        login();

        delete(getEntityBasePath()+getSomeIdOfExistingEntity());
        assertStatusCode(200);
    }
}
