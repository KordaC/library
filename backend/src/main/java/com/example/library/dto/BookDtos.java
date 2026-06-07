package com.example.library.dto;

import java.util.List;

public final class BookDtos {

    private BookDtos() {}

    public record GenreItem(String id, String name) {}

    public record BookListItem(
            String id,
            String title,
            String authorName,
            int availableCount,
            Integer publicationYear,
            String isbn,
            String coverImageUrl
    ) {}

    public record BookDetail(
            String id,
            String title,
            String authorName,
            String description,
            Integer publicationYear,
            String isbn,
            int availableCount,
            int totalCopies,
            List<String> genres,
            String coverImageUrl
    ) {}

    public record RenewResponse(String loanId, String newDueDate, int renewalCount) {}
}
