package com.hedgehogsmind.springcouchrest.integration.env.crud;

import com.hedgehogsmind.springcouchrest.annotations.EnableCouchRest;
import com.hedgehogsmind.springcouchrest.annotations.crud.CrudMethods;

import javax.persistence.Entity;

@Entity
@EnableCouchRest
@CrudMethods(
        get = false,
        saveUpdate = false,
        delete = false
)
public class TestNoteEntityWithDisabledCrudMethods extends AbstractTestNoteEntity {

}
