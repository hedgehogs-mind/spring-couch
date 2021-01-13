package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityWithCrudSecurityWithoutBaseRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public class CouchRestCrudDisabledBaseSecurityRuleTest extends CouchRestCrudSecurityTestBase {

    @Repository
    public interface TestNoteEntityWithCrudSecurityWithoutBaseRuleRepository
            extends CrudRepository<TestNoteEntityWithCrudSecurityWithoutBaseRule, Long> {
    }

    @Autowired
    protected CouchRestCrudDisabledBaseSecurityRuleTest.TestNoteEntityWithCrudSecurityWithoutBaseRuleRepository repository;

    private List<TestNoteEntityWithCrudSecurityWithoutBaseRule> entities;

    @BeforeEach
    public void setupNewDummyData() {
        repository.save(new TestNoteEntityWithCrudSecurityWithoutBaseRule("First note", "This is a note for CouchRest testing.", 2));
        repository.save(new TestNoteEntityWithCrudSecurityWithoutBaseRule("Pinned information", "CouchRest is a simple to use Spring addition to publish entities via REST.", 5));
        repository.save(new TestNoteEntityWithCrudSecurityWithoutBaseRule("Shopping list", "- carrots\n- sugar\n- yeast\n- 2 minions", 3));

        entities = new ArrayList<>();
        repository.findAll().forEach(entities::add);
    }

    @AfterEach
    public void cleanupNewDummyData() {
        repository.deleteAll();
        entities.clear();
    }

    @Override
    protected String getEntityBasePath() {
        return getBasePath() + "testNoteEntityWithCrudSecurityWithoutBaseRule/";
    }

    @Override
    protected Object getSomeIdOfExistingEntity() {
        return entities.get(0).id;
    }

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
