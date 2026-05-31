package com.example.library.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "book_genre")
@IdClass(BookGenre.BookGenreId.class)
public class BookGenre {

    @Id
    @Column(name = "book_id")
    private UUID bookId;

    @Id
    @Column(name = "genre_id")
    private UUID genreId;

    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    public UUID getGenreId() { return genreId; }
    public void setGenreId(UUID genreId) { this.genreId = genreId; }

    public static class BookGenreId implements Serializable {
        private UUID bookId;
        private UUID genreId;

        public BookGenreId() {}

        public BookGenreId(UUID bookId, UUID genreId) {
            this.bookId = bookId;
            this.genreId = genreId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookGenreId that = (BookGenreId) o;
            return bookId.equals(that.bookId) && genreId.equals(that.genreId);
        }

        @Override
        public int hashCode() {
            return bookId.hashCode() * 31 + genreId.hashCode();
        }
    }
}
