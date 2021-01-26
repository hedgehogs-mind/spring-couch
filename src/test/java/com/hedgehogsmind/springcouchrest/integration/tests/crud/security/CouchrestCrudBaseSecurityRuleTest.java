package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntity;

public class CouchrestCrudBaseSecurityRuleTest extends CouchRestCrudSecurityTestBase<TestNoteEntity> {

    @Override
    protected String getNeededAuthority() {
        return "API_BASE";
    }

    @Override
    protected String getBaseSecurityRule() {
        return "isAuthenticated() && hasAuthority('"+getNeededAuthority()+"')";
    }

    @Override
    protected String getDefaultEndpointSecurityRule() {
        return "permitAll()";
    }
}
