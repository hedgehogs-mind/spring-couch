package com.hedgehogsmind.springcouchrest.integration.env.crud;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.security.CrudSecurity;

import javax.persistence.Entity;

@Entity
@CouchRest(checkBaseSecurityRule = false)
@CrudSecurity(
        read = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurityWithoutBaseRule.CRUD_AUTHORITY+"')",
        saveUpdate = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurityWithoutBaseRule.CRUD_AUTHORITY+"')",
        delete = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurityWithoutBaseRule.CRUD_AUTHORITY+"')"
)
public class TestNoteEntityWithCrudSecurityWithoutBaseRule extends AbstractTestNoteEntity {

    public static final String CRUD_AUTHORITY = "CRUD_TEST_NOTE_AUTHORITY";

    public TestNoteEntityWithCrudSecurityWithoutBaseRule() {
    }

    public TestNoteEntityWithCrudSecurityWithoutBaseRule(String title, String content, int rating) {
        super(title, content, rating);
    }
}
