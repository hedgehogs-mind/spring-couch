package com.hedgehogsmind.springcouchrest.integration.tests.crud.security;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityWithCrudSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public class CouchRestCrudMethodSecurityRulesTest
        extends CouchRestCrudSecurityTestBase {

    @Repository
    public interface TestNoteEntityWithCrudSecurityRepository
            extends CrudRepository<TestNoteEntityWithCrudSecurity, Long> {
    }

    @Autowired
    protected TestNoteEntityWithCrudSecurityRepository repository;

    private List<TestNoteEntityWithCrudSecurity> entities;

    @BeforeEach
    public void setupNewDummyData() {
        repository.save(new TestNoteEntityWithCrudSecurity("First note", "This is a note for CouchRest testing.", 2));
        repository.save(new TestNoteEntityWithCrudSecurity("Pinned information", "CouchRest is a simple to use Spring addition to publish entities via REST.", 5));
        repository.save(new TestNoteEntityWithCrudSecurity("Shopping list", "- carrots\n- sugar\n- yeast\n- 2 minions", 3));

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
        return getBasePath() + "testNoteEntityWithCrudSecurity/";
    }

    @Override
    protected Object getSomeIdOfExistingEntity() {
        return entities.get(0).id;
    }

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
