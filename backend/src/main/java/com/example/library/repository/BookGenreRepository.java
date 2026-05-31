package com.example.library.repository;

import com.example.library.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookGenreRepository extends JpaRepository<BookGenre, BookGenre.BookGenreId> {

    @Query("""
            SELECT g.name FROM Genre g, BookGenre bg
            WHERE bg.bookId = :bookId AND bg.genreId = g.id
            """)
    List<String> findGenreNamesByBookId(@Param("bookId") UUID bookId);
}
