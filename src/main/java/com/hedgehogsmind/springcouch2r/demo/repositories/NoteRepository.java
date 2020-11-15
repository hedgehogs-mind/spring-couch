package com.hedgehogsmind.springcouch2r.demo.repositories;

import com.hedgehogsmind.springcouch2r.demo.entities.Note;
import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Couch2r
@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {

}
