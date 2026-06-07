package com.example.applibrary.data.remote.dto;

import java.util.List;

public final class CatalogDtos {

    private CatalogDtos() {}

    public static class GenreItem {
        public String id;
        public String name;
    }

    public static class BookListItem {
        public String id;
        public String title;
        public String authorName;
        public int availableCount;
        public Integer publicationYear;
        public String isbn;
        public String coverImageUrl;
    }

    public static class BookDetail {
        public String id;
        public String title;
        public String authorName;
        public String description;
        public Integer publicationYear;
        public String isbn;
        public int availableCount;
        public int totalCopies;
        public List<String> genres;
        public String coverImageUrl;
    }
}
