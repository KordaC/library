package com.example.library.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "book")
public class Book {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "author_name")
    private String authorName;

    private String description;

    @Column(name = "publication_year")
    private Integer publicationYear;

    private String isbn;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
