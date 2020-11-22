package com.hedgehogsmind.springcouch2r.workers.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import org.apache.coyote.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MappingTestBase {

    @SpringBootApplication(scanBasePackages = {}, exclude = {Couch2rCore.class, Couch2rConfiguration.class})
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class App {

        @Autowired
        public ApplicationContext applicationContext;

        @Autowired
        public EntityManager entityManager;

        @Bean
        public Couch2rConfiguration config() {
            return new Couch2rConfiguration() {
                @Override
                public String getCouch2rBasePath() {
                    return "/couch2r/";
                }

                @Override
                public Optional<ObjectMapper> getCouch2rObjectMapper() {
                    return Optional.empty();
                }
            };
        }

        @Bean
        public Couch2rCore core() {
            return new Couch2rCore(applicationContext, entityManager, Optional.empty());
        }
    }

    @Entity
    @Couch2r
    public static class TestNote {
        @Id @GeneratedValue
        public long id;

        @Column
        public String title;

        @Column
        public String content;

        @Column
        public int rating;

        public TestNote(String title, String content, int rating) {
            this.title = title;
            this.content = content;
            this.rating = rating;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestNote testNote = (TestNote) o;
            return id == testNote.id &&
                    rating == testNote.rating &&
                    Objects.equals(title, testNote.title) &&
                    Objects.equals(content, testNote.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, title, content, rating);
        }

        @Override
        public String toString() {
            return "TestNote{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", rating=" + rating +
                    '}';
        }
    }

    @Autowired
    public Couch2rCore core;

    public Couch2rMapping noteMapping;

    public ObjectMapper om;

    public List<TestNote> persistedTestNotes;

    @BeforeAll
    public void setup() {
        if ( core.getCouch2rMappings().size() != 1 ) {
            throw new IllegalStateException("Expected only one mapping > TestNote mapping");
        }

        noteMapping = core.getCouch2rMappings().stream().findAny().get();

        if ( noteMapping.getEntityType().getJavaType() != TestNote.class ) {
            throw new IllegalStateException("One mapping in Couch2rCore was not for TestNote, instead "
                        + noteMapping.getEntityType().getJavaType());
        }

        om = core.getCouch2rObjectMapper();
    }

    @BeforeEach
    public void setupDummyData() {
        final CrudRepository<TestNote, Long> repo = noteMapping.getRepository();

        repo.save(new TestNote("First note", "This is a note for Couch2r testing.", 2));
        repo.save(new TestNote("Pinned information", "Couch2r is a simple to use Spring addition to publish entities via REST.", 5));
        repo.save(new TestNote("Shopping list", "- carrots\n- sugar\n- yeast\n- 2 minions", 3));

        persistedTestNotes = new ArrayList<>();
        repo.findAll().forEach(persistedTestNotes::add);
    }

    /**
     * Deletes all existing test notes.
     */
    @AfterEach
    public void cleanupDummyData() {
        final CrudRepository<TestNote, Long> repo = noteMapping.getRepository();
        repo.deleteAll();
    }

    /**
     * Mocks an HTTP Request.
     *
     * @param method HTTP method. Can be lower case.
     * @param pathAppendix Optional. If not null, it will be appended to the noteMapping basePath.
     * @return Mocked request.
     */
    protected HttpServletRequest mockRequest(final String method, final String pathAppendix) {
        final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);

        when(mockedRequest.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.ENGLISH)));
        when(mockedRequest.getLocale()).thenReturn(Locale.ENGLISH);

        String path = noteMapping.getPathWithTrailingSlash();
        if ( pathAppendix != null ) path += pathAppendix;

        when(mockedRequest.getContextPath()).thenReturn("");
        when(mockedRequest.getRequestURI()).thenReturn(path);
        when(mockedRequest.getMethod()).thenReturn(method.toUpperCase());

        return mockedRequest;
    }

    /**
     * Calls {@link #mockRequest(String)} without path appendix.
     *
     * @param method Method. Can be lower case.
     * @return Mocked request.
     */
    protected HttpServletRequest mockRequest(final String method) {
        return mockRequest(method,null);
    }

    /**
     * Convenience method. Calls {@link Couch2rMapping#handle(HttpServletRequest, ObjectMapper)} with
     * mocked request and object mapper of Couch2rConfiguration.
     *
     * @param method Method to use. Can be lower case.
     * @param pathAppendix Optional. If not null, it will be appended to resource base path.
     * @return Couch2rMapping handle result.
     */
    protected ResponseEntity perform(final String method, final String pathAppendix) {
        return noteMapping.handle(mockRequest(method, pathAppendix), om);
    }

    /**
     * Convenience method. Like {@link #perform(String, String)} but without appendix.
     *
     * @param method Method to use. Can be lower case.
     * @return Couch2rMapping handle result.
     */
    protected ResponseEntity perform(final String method) {
        return noteMapping.handle(mockRequest(method), om);
    }

}
