package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntity;

public class CouchRestCrudDefaultEndpointSecurityRuleTest extends CouchRestCrudSecurityTestBase<TestNoteEntity> {

    @Override
    protected String getNeededAuthority() {
        return "API_DEFAULT_EP";
    }

    @Override
    protected String getBaseSecurityRule() {
        return "permitAll()";
    }

    @Override
    protected String getDefaultEndpointSecurityRule() {
        return "isAuthenticated() && hasAuthority('"+getNeededAuthority()+"')";
    }

}
