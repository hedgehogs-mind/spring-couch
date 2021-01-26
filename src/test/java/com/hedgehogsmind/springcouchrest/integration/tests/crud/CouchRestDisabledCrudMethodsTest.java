package com.hedgehogsmind.springcouchrest.integration.tests.crud;

import com.hedgehogsmind.springcouchrest.integration.env.crud.TestNoteEntityWithDisabledCrudMethods;
import org.junit.jupiter.api.Test;

public class CouchRestDisabledCrudMethodsTest
        extends CouchRestAbstractCrudIntegrationTestBase<TestNoteEntityWithDisabledCrudMethods>  {

    @Test
    public void testGetAll404() {
        final String body = get(getNoteBasePath());
        System.out.println(body);
        assertStatusCode(404);
    }

    @Test
    public void testGetOne404() {
        get(getNoteBasePath()+"2");
        assertStatusCode(404);
    }

    @Test
    public void testSave404() {
        post(getNoteBasePath(), "{}");
        assertStatusCode(404);
    }

    @Test
    public void testUpdate404() {
        post(getNoteBasePath()+"2", "{}");
        assertStatusCode(404);
    }

    @Test
    public void testDelete404() {
        delete(getNoteBasePath()+"2");
        assertStatusCode(404);
    }

}
