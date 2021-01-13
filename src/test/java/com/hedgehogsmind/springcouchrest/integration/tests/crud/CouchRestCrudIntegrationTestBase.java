package com.hedgehogsmind.springcouchrest.integration.tests.crud;

import com.hedgehogsmind.springcouchrest.integration.CouchRestIntegrationTestBase;
import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntity;
import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class CouchRestCrudIntegrationTestBase
        extends CouchRestIntegrationTestBase {

    @Autowired
    public TestNoteEntityRepository noteRepository;

    public List<TestNoteEntity> persistedTestNotes;

    @BeforeEach
    public void setupDummyData() {
        noteRepository.save(new TestNoteEntity("First note", "This is a note for CouchRest testing.", 2));
        noteRepository.save(new TestNoteEntity("Pinned information", "CouchRest is a simple to use Spring addition to publish entities via REST.", 5));
        noteRepository.save(new TestNoteEntity("Shopping list", "- carrots\n- sugar\n- yeast\n- 2 minions", 3));

        persistedTestNotes = new ArrayList<>();
        noteRepository.findAll().forEach(persistedTestNotes::add);
    }

    /**
     * Deletes all existing test notes.
     */
    @AfterEach
    public void cleanupDummyData() {
        noteRepository.deleteAll();
    }

    protected String getNoteBasePath() {
        return getBasePath() + "testNoteEntity/";
    }

    protected long getNoteEntityCount() {
        return noteRepository.count();
    }

    protected long getSomeNoteEntityId() {
        return persistedTestNotes.get(0).id;
    }

}
