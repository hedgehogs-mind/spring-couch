package com.hedgehogsmind.springcouchrest.integration.env.crud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestNoteEntityRepository extends CrudRepository<TestNoteEntity, Long> {


}
