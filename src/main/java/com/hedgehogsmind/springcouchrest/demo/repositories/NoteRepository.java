package com.hedgehogsmind.springcouchrest.demo.repositories;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.demo.entities.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@CouchRest
@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {

}
