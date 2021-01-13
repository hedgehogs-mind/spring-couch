package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

public class CouchrestCrudBaseSecurityRuleTest extends CouchRestCrudSecurityTestBase {

    @Override
    protected String getEntityBasePath() {
        return getNoteBasePath();
    }

    @Override
    protected Object getSomeIdOfExistingEntity() {
        return getSomeNoteEntityId();
    }

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
