package com.hedgehogsmind.springcouchrest.demo.repositories;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.security.CrudSecurity;
import com.hedgehogsmind.springcouchrest.demo.entities.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@CouchRest
@CrudSecurity(read = "permitAll()", saveUpdate = "permitAll()", delete = "permitAll()")
@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {

}
