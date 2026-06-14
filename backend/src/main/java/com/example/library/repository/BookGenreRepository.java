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

    @Query("SELECT bg.genreId FROM BookGenre bg WHERE bg.bookId = :bookId")
    List<UUID> findGenreIdsByBookId(@Param("bookId") UUID bookId);

    @Query("SELECT DISTINCT bg.bookId FROM BookGenre bg WHERE bg.genreId IN :genreIds")
    List<UUID> findBookIdsByGenreIds(@Param("genreIds") List<UUID> genreIds);
}
