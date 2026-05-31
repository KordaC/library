package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.CatalogDtos;

import java.util.List;

public class CatalogRepository {

    private final LibraryApi api;

    public CatalogRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<List<CatalogDtos.GenreItem>> loadGenres() {
        return ApiCallHandler.execute(api.genres());
    }

    public ApiResult<List<CatalogDtos.BookListItem>> loadBooks(String q, String genreId, String sort) {
        return ApiCallHandler.execute(api.books(q, genreId, sort));
    }

    public ApiResult<CatalogDtos.BookDetail> loadBook(String id) {
        return ApiCallHandler.execute(api.book(id));
    }
}
