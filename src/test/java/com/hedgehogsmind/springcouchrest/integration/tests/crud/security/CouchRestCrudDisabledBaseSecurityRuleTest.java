package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityWithCrudSecurityWithoutBaseRule;

public class CouchRestCrudDisabledBaseSecurityRuleTest extends CouchRestCrudSecurityTestBase<TestNoteEntityWithCrudSecurityWithoutBaseRule> {

    @Override
    protected String getNeededAuthority() {
        return TestNoteEntityWithCrudSecurityWithoutBaseRule.CRUD_AUTHORITY;
    }

    @Override
    protected String getBaseSecurityRule() {
        // TestNoteEntityWithCrudSecurityWithoutBaseRule's @CouchRest has the flag
        // checkBaseSecurityRule set to "false" > this rule should be ignored
        return "isAuthenticated() && hasAuthority('SOME_AUTHORITY_THE_USER_WILL__N_E_V_E_R__HAVE')";
    }

    @Override
    protected String getDefaultEndpointSecurityRule() {
        return "denyAll()"; // should be overridden by the @CrudSecurity rules > would fail if not
    }
}
