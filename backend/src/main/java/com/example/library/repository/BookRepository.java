package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @Query("""
            SELECT DISTINCT b FROM Book b
            WHERE (:q IS NULL OR :q = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(b.authorName, '')) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:genreId IS NULL OR EXISTS (
                SELECT 1 FROM BookGenre bg WHERE bg.bookId = b.id AND bg.genreId = :genreId))
            ORDER BY b.title ASC
            """)
    List<Book> search(@Param("q") String q, @Param("genreId") UUID genreId);
}
