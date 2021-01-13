package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

public class CouchRestCrudDefaultEndpointSecurityRuleTest extends CouchRestCrudSecurityTestBase {

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
