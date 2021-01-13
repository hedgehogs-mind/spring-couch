package com.hedgehogsmind.springcouchrest.integration.env.crud;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;

import javax.persistence.Entity;

@Entity
@CouchRest
public class TestNoteEntity extends AbstractTestNoteEntity {

    public TestNoteEntity() {
    }

    public TestNoteEntity(String title, String content, int rating) {
        super(title, content, rating);
    }

}
