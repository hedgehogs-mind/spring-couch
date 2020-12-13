package com.hedgehogsmind.springcouch2r.workers.mapping.integration.env;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestNoteEntityRepository extends CrudRepository<TestNoteEntity, Long> {


}
