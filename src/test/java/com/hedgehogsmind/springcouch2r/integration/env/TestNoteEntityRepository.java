package com.hedgehogsmind.springcouch2r.integration.env;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestNoteEntityRepository extends CrudRepository<TestNoteEntity, Long> {


}
