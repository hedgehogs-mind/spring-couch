package com.hedgehogsmind.springcouchrest.integration.tests.crud;

import com.hedgehogsmind.springcouchrest.integration.CouchRestIntegrationTestBase;
import com.hedgehogsmind.springcouchrest.integration.env.crud.AbstractTestNoteEntity;
import com.hedgehogsmind.springcouchrest.util.EntityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public abstract class CouchRestAbstractCrudIntegrationTestBase<ET extends AbstractTestNoteEntity>
        extends CouchRestIntegrationTestBase {

    public CouchRestAbstractCrudIntegrationTestBase() {
        this.noteType =
                (Class<ET>) GenericTypeResolver.resolveTypeArgument(getClass(), CouchRestAbstractCrudIntegrationTestBase.class);
    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    protected final Class<ET> noteType;

    protected CrudRepository<ET, Long> noteRepository;

    protected List<ET> persistedTestNotes;

    @BeforeAll
    public void beforeAll() {
        this.noteRepository = new SimpleJpaRepository<ET, Long>(this.noteType, entityManager);

        this.noteRepository = (CrudRepository<ET, Long>) applicationContext.getAutowireCapableBeanFactory().initializeBean(
                this.noteRepository,
                "CrudRepo"
        );
    }

    protected ET newInstance() {
        try {
            return this.noteType.getDeclaredConstructor().newInstance();
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException("Can note create new instance of entity type: "+this.noteType.getName(), e);
        } catch ( Throwable e ) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    protected ET newInstance(final String title, final String content, final int rating) {
        final ET instance = newInstance();

        instance.title = title;
        instance.content = content;
        instance.rating = rating;

        return instance;
    }

    @BeforeEach
    public void setupDummyData() {
        noteRepository.save(newInstance("First note", "This is a note for CouchRest testing.", 2));
        noteRepository.save(newInstance("Pinned information", "CouchRest is a simple to use Spring addition to publish entities via REST.", 5));
        noteRepository.save(newInstance("Shopping list", "- carrots\n- sugar\n- yeast\n- 2 minions", 3));

        persistedTestNotes = new ArrayList<>();
        noteRepository.findAll().forEach(persistedTestNotes::add);
    }

    /**
     * Deletes all existing test notes.
     */
    @AfterEach
    public void cleanupDummyData() {
        noteRepository.deleteAll();
    }

    protected String getNoteBasePath() {
        return getBasePath() + EntityUtil.getEntityClassNameSnakeCase(this.noteType) + "/";
    }

    protected long getNoteEntityCount() {
        return noteRepository.count();
    }

    protected long getSomeNoteEntityId() {
        return persistedTestNotes.get(0).id;
    }

}
