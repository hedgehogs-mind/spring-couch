package com.hedgehogsmind.springcouch2r.demo;

import com.hedgehogsmind.springcouch2r.demo.entities.Address;
import com.hedgehogsmind.springcouch2r.demo.entities.Note;
import com.hedgehogsmind.springcouch2r.demo.repositories.AddressRepository;
import com.hedgehogsmind.springcouch2r.demo.repositories.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DummyInitializer {

    private final NoteRepository noteRepository;

    private final AddressRepository addressRepository;

    @PostConstruct
    public void createDummyNotes() {
        noteRepository.save(
                Note.builder().title("Testnotiz 1").content("Das ist meine erste Testnotiz.").build()
        );

        noteRepository.save(
                Note.builder().title("Zweite Notiz").content("Heute ist ein wunderschöner Tag.").build()
        );

        addressRepository.save(
                Address.builder().street("Lotzestr.").nr("43").zip("37083").town("Göttingen").build()
        );

        addressRepository.save(
                Address.builder().street("Frankfurter Str.").nr("141").zip("34121").town("Kassel").build()
        );
    }

}
