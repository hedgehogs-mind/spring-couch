package com.hedgehogsmind.springcouch2r.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.integration.Couch2rIntegrationTestBase;
import com.hedgehogsmind.springcouch2r.integration.env.TestNoteEntity;
import com.hedgehogsmind.springcouch2r.integration.env.TestNoteEntityRepository;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    void testAddingNewEntity() throws JsonProcessingException {
        final long countBefore = noteRepository.count();

        final JSONObject response = postWithJsonObjectResponse(
                getNoteBasePath(),
                "{ \"title\": \"123\", \"content\": \"456\", \"rating\": 789 }"
        );

        Assertions.assertEquals(200, lastStatusCode, "failed");

        Assertions.assertTrue(response.has("id"), "No id has been returned");
        Assertions.assertEquals("123", response.getString("title"));
        Assertions.assertEquals("456", response.getString("content"));
        Assertions.assertEquals(789, response.getInt("rating"));

        Assertions.assertEquals(countBefore+1, noteRepository.count(), "TestNotEntity count not correct");

        final ObjectMapper objectMapper = new ObjectMapper();
        final TestNoteEntity returnedEntity = objectMapper.readValue(response.toString(), TestNoteEntity.class);
        final Optional<TestNoteEntity> persistedEntity = noteRepository.findById(returnedEntity.id);

        Assertions.assertTrue(persistedEntity.isPresent(), "new TestNoteEntity has not been persisted");
        Assertions.assertEquals(returnedEntity, persistedEntity.get(), "Persisted entity does not match " +
                "returned entity (expected parameter)");
    }

    @Test
    void testUpdatingEntityThroughPathVariableId() {
        final TestNoteEntity entity = persistedTestNotes.get(0);
        final long countBefore = noteRepository.count();
        final String newContent = "Totally new : "+System.currentTimeMillis();

        final JSONObject response = postWithJsonObjectResponse(getNoteBasePath()+entity.id,
                "{ \"title\": \""+entity.title+"\", \"content\": \""+newContent+"\", \"rating\": "+entity.rating+" }");

        Assertions.assertEquals(countBefore, noteRepository.count(), "entity count changed");
        Assertions.assertEquals(entity.id, response.getLong("id"));
        Assertions.assertEquals(newContent, response.getString("content"));

        final TestNoteEntity entityFetchedAfterSave = noteRepository.findById(entity.id).get();
        Assertions.assertEquals(newContent, entityFetchedAfterSave.content);
    }

    @Test
    void testUpdatingSubSetOfFields() {
        final TestNoteEntity entity = persistedTestNotes.get(0);
        final long countBefore = noteRepository.count();

        final String oldTitle = entity.title;
        final int oldRating = entity.rating;

        final String newContent = "Totally new : "+System.currentTimeMillis();

        final JSONObject response = postWithJsonObjectResponse(getNoteBasePath()+entity.id,
                "{ \"content\": \""+newContent+"\" }"); // we update only one field

        Assertions.assertEquals(countBefore, noteRepository.count(), "entity count changed");
        Assertions.assertEquals(entity.id, response.getLong("id"));
        Assertions.assertEquals(newContent, response.getString("content"));

        final TestNoteEntity entityFetchedAfterSave = noteRepository.findById(entity.id).get();
        Assertions.assertEquals(newContent, entityFetchedAfterSave.content);
        Assertions.assertEquals(oldTitle, entityFetchedAfterSave.title);
        Assertions.assertEquals(oldRating, entityFetchedAfterSave.rating);
    }

    @Test
    void testUpdatingEntityThroughIdInBody() {
        final TestNoteEntity entity = persistedTestNotes.get(0);
        final long countBefore = noteRepository.count();
        final String newContent = "Totally new : "+System.currentTimeMillis();

        final JSONObject response = postWithJsonObjectResponse(getNoteBasePath(), // important > no path variable!
                "{ \"id\": " + entity.id + "," + // here we set id
                        "\"title\": \""+entity.title+"\", \"content\": \""+newContent+"\", \"rating\": "+entity.rating+" }");

        Assertions.assertEquals(countBefore, noteRepository.count(), "entity count changed");
        Assertions.assertEquals(entity.id, response.getLong("id"));
        Assertions.assertEquals(newContent, response.getString("content"));

        final TestNoteEntity entityFetchedAfterSave = noteRepository.findById(entity.id).get();
        Assertions.assertEquals(newContent, entityFetchedAfterSave.content);
    }

    @Test
    void testInvalidJson() {
        final String invalidData = "content : hello";
        final JSONObject response = postWithJsonObjectResponse(getNoteBasePath(), invalidData);

        assertProblemDetailGiven(Couch2rProblems.INVALID_DATA, response);
    }

    @Test
    void testPostWrongIdType() {
        final JSONObject response = postWithJsonObjectResponse(getNoteBasePath()+"abc", "{}");
        assertProblemDetailGiven(Couch2rProblems.WRONG_ID_TYPE, response);
    }
}
