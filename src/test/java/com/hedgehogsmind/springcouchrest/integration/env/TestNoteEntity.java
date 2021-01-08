package com.hedgehogsmind.springcouchrest.integration.env;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@CouchRest
public class TestNoteEntity {

    @Id
    @GeneratedValue
    public long id;

    @Column
    public String title;

    @Column
    public String content;

    @Column
    public int rating;

    public TestNoteEntity() {
    }

    public TestNoteEntity(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestNoteEntity that = (TestNoteEntity) o;
        return id == that.id && rating == that.rating && Objects.equals(title, that.title) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, rating);
    }

    @Override
    public String toString() {
        return "TestNoteEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                '}';
    }
}
