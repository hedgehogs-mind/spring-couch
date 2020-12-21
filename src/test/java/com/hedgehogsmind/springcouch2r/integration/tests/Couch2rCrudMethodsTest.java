package com.hedgehogsmind.springcouch2r.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.integration.Couch2rIntegrationTestBase;
import com.hedgehogsmind.springcouch2r.integration.env.TestNoteEntity;
import com.hedgehogsmind.springcouch2r.integration.env.TestNoteEntityRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class Couch2rCrudMethodsTest extends Couch2rIntegrationTestBase {

    @Autowired
    public TestNoteEntityRepository noteRepository;

    public List<TestNoteEntity> persistedTestNotes;

    @BeforeEach
    public void setupDummyData() {
        noteRepository.save(new TestNoteEntity("First note", "This is a note for Couch2r testing.", 2));
        noteRepository.save(new TestNoteEntity("Pinned information", "Couch2r is a simple to use Spring addition to publish entities via REST.", 5));
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

    @Test
    public void testGetAll() throws JsonProcessingException  {
        final JSONArray response = getWithJsonArrayResponse(getNoteBasePath());

        Assertions.assertEquals(persistedTestNotes.size(), response.length());

        final List<TestNoteEntity> allNotes = new ArrayList<>(persistedTestNotes);
        for ( final Object element : response ) {
            final JSONObject jsonNote = (JSONObject) element;
            final TestNoteEntity note = core.getCouch2rObjectMapper().readValue(jsonNote.toString(), TestNoteEntity.class);

            allNotes.remove(note);
        }

        Assertions.assertTrue(allNotes.isEmpty(), "Mismatch between persisted notes and serialized ones");
    }

    @Test
    public void testGetOne() throws JsonProcessingException {
        final long noteId = persistedTestNotes.get(0).id;
        final String response = get(getNoteBasePath()+noteId);

        final TestNoteEntity note = core.getCouch2rObjectMapper().readValue(response, TestNoteEntity.class);

        Assertions.assertEquals(persistedTestNotes.get(0), note);
    }

    @Test
    public void testGetWrongIdType() {
        final JSONObject response = getWithJsonObjectResponse(getNoteBasePath()+"abc");
        assertProblemDetailGiven(Couch2rProblems.WRONG_ID_TYPE, response);
    }

    @Test
    public void testMethodNotKnown() {
        // under this path is (probably) never a mapping available
        final JSONObject response = getWithJsonObjectResponse(getNoteBasePath()+"1/2/3/4/5/6/7/8/9");
        assertProblemDetailGiven(Couch2rProblems.NOT_FOUND, response);
    }

}
