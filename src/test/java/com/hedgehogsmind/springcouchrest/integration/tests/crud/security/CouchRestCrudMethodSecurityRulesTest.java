package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityWithCrudSecurity;

public class CouchRestCrudMethodSecurityRulesTest
        extends CouchRestCrudSecurityTestBase<TestNoteEntityWithCrudSecurity> {

    @Override
    protected String getNeededAuthority() {
        return TestNoteEntityWithCrudSecurity.CRUD_AUTHORITY;
    }

    @Override
    protected String getBaseSecurityRule() {
        return "permitAll()";
    }

    @Override
    protected String getDefaultEndpointSecurityRule() {
        return "denyAll()"; // should be overridden by the @CrudSecurity rules > would fail if not
    }

}
