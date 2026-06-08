package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BookDtos;
import com.example.library.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final CatalogService catalogService;

    public BookController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/genres")
    public ApiResponse<List<BookDtos.GenreItem>> genres() {
        return ApiResponse.ok(catalogService.listGenres());
    }

    @GetMapping("/books")
    public ApiResponse<List<BookDtos.BookListItem>> books(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID genreId,
            @RequestParam(required = false, defaultValue = "title") String sort
    ) {
        return ApiResponse.ok(catalogService.listBooks(q, genreId, sort));
    }

    @GetMapping("/books/{id}")
    public ApiResponse<BookDtos.BookDetail> book(@PathVariable UUID id) {
        return ApiResponse.ok(catalogService.getBook(id));
    }

    @PatchMapping("/books/{id}/cover")
    public ApiResponse<BookDtos.BookDetail> updateCover(
            @PathVariable UUID id,
            @RequestBody BookDtos.UpdateCoverRequest request
    ) {
        return ApiResponse.ok(catalogService.updateCover(id, request.coverImageUrl()));
    }
}
