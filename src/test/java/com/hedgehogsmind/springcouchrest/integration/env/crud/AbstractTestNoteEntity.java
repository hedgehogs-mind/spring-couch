package com.hedgehogsmind.springcouchrest.integration.env.crud;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class AbstractTestNoteEntity {

    @Id
    @GeneratedValue
    public long id;

    @Column
    public String title;

    @Column
    public String content;

    @Column
    public int rating;

    public AbstractTestNoteEntity() {
    }

    public AbstractTestNoteEntity(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTestNoteEntity that = (AbstractTestNoteEntity) o;
        return id == that.id && rating == that.rating && Objects.equals(title, that.title) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, rating);
    }

    @Override
    public String toString() {
        return "AbstractTestNoteEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                '}';
    }
}
