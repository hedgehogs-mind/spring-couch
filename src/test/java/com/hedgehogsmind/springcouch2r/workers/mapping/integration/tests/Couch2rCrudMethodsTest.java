package com.hedgehogsmind.springcouch2r.workers.mapping.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertible;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.workers.mapping.integration.Couch2rIntegrationTestBase;
import com.hedgehogsmind.springcouch2r.workers.mapping.integration.env.TestNoteEntity;
import com.hedgehogsmind.springcouch2r.workers.mapping.integration.env.TestNoteEntityRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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
        perform(mockRequest("GET", getNoteBasePath()));
        final JSONArray response = getResponseJsonArray();

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
        perform(mockRequest("GET", getNoteBasePath()+noteId));

        final TestNoteEntity note = core.getCouch2rObjectMapper().readValue(getResponseBody(), TestNoteEntity.class);

        Assertions.assertEquals(persistedTestNotes.get(0), note);
    }

//    @Test
//    public void testGetTooManyPathVariables() {
//        final ResponseEntity re = perform("GET", "/1/2");
//
//        Assertions.assertTrue(re.getBody() instanceof ProblemDetailConvertible);
//        Assertions.assertEquals(Couch2rProblems.TOO_MANY_PATH_VARIABLES, re.getBody());
//    }

    @Test
    public void testGetWrongIdType() {
        perform(mockRequest("GET", getNoteBasePath()+"abc"));

        final JSONObject response = getResponseJson();
        Assertions.assertTrue(response.has("type"));
        Assertions.assertEquals(Couch2rProblems.WRONG_ID_TYPE.getType().toString(), response.getString("type"));
    }

}
