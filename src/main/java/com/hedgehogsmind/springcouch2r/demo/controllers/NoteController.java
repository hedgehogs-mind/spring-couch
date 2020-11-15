package com.hedgehogsmind.springcouch2r.demo.controllers;

import com.hedgehogsmind.springcouch2r.demo.entities.Note;
import com.hedgehogsmind.springcouch2r.demo.repositories.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository noteRepository;

    @GetMapping
    public Iterable<Note> getAll() {
        return noteRepository.findAll();
    }

}
