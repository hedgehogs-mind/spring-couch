package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.I18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertible;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

public class Couch2rMappingTest extends MappingTestBase {

    @Test
    public void testGetAll() {
        final ResponseEntity re = perform("GET");

        Assertions.assertTrue(re.getBody() instanceof List);
        final List<TestNote> result = (List) re.getBody();

        Assertions.assertEquals(persistedTestNotes.size(), result.size());

        for ( final TestNote expectedNote : persistedTestNotes ) {
            if ( !result.contains(expectedNote) ) {
                Assertions.fail("GET all request did not contain expected note: "+expectedNote);
            }
        }
    }

    @Test
    public void testGetOne() {
        final long noteId = persistedTestNotes.get(0).id;
        final ResponseEntity re = perform("GET", ""+noteId);

        Assertions.assertTrue(re.getBody() instanceof TestNote);
        Assertions.assertEquals(persistedTestNotes.get(0), re.getBody());
    }

    @Test
    public void testGetTooManyPathVariables() {
        final ResponseEntity re = perform("GET", "/1/2");

        Assertions.assertTrue(re.getBody() instanceof ProblemDetailConvertible);
        Assertions.assertEquals(Couch2rProblems.TOO_MANY_PATH_VARIABLES, re.getBody());
    }

    @Test
    public void testGetWrongIdType() {
        final ResponseEntity re = perform("GET", "/abc");

        Assertions.assertTrue(re.getBody() instanceof ProblemDetailConvertible);
        Assertions.assertEquals(Couch2rProblems.WRONG_ID_TYPE, re.getBody());
    }

    // TODO @peter todo
    /*
    @Test
    public void testGetUnsupportedIdType() {
        final ResponseEntity re = perform("GET", "/entityWithUnsupportedIdType/972136");

        Assertions.assertTrue(re.getBody() instanceof ProblemDetailConvertible);
        Assertions.assertEquals(Couch2rProblems.ID_TYPE_PARSING_NOT_SUPPORTED.getProblemType(),
                ((ProblemDetailConvertible) re.getBody()).toProblemDetail(Locale.GERMAN).getType());
    }
    */


}
