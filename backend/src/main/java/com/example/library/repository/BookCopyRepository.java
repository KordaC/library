package com.example.library.repository;

import com.example.library.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
    long countByBookIdAndStatus(UUID bookId, String status);
    long countByBookId(UUID bookId);
    Optional<BookCopy> findFirstByBookIdAndStatusOrderByInventoryNumberAsc(UUID bookId, String status);
    List<BookCopy> findByBookIdAndStatus(UUID bookId, String status);
}
