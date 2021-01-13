package com.hedgehogsmind.springcouchrest.integration.env.crud;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.security.CrudSecurity;

import javax.persistence.Entity;

@Entity
@CouchRest
@CrudSecurity(
        read = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurity.CRUD_AUTHORITY+"')",
        saveUpdate = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurity.CRUD_AUTHORITY+"')",
        delete = "isAuthenticated() && hasAuthority('"+TestNoteEntityWithCrudSecurity.CRUD_AUTHORITY+"')"
)
public class TestNoteEntityWithCrudSecurity extends AbstractTestNoteEntity {

    public static final String CRUD_AUTHORITY = "CRUD_TEST_NOTE_AUTHORITY";

    public TestNoteEntityWithCrudSecurity() {
    }

    public TestNoteEntityWithCrudSecurity(String title, String content, int rating) {
        super(title, content, rating);
    }
}
