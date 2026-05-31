package com.example.applibrary.ui.catalog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.CatalogRepository;

import java.util.ArrayList;
import java.util.List;

public class CatalogViewModel extends ViewModel {

    private final CatalogRepository catalogRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<CatalogDtos.BookListItem>> books = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CatalogDtos.GenreItem>> genres = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<CatalogDtos.BookDetail> bookDetail = new MutableLiveData<>();

    public CatalogViewModel(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<List<CatalogDtos.BookListItem>> getBooks() { return books; }
    public LiveData<List<CatalogDtos.GenreItem>> getGenres() { return genres; }
    public LiveData<CatalogDtos.BookDetail> getBookDetail() { return bookDetail; }

    public void loadGenres() {
        new Thread(() -> {
            ApiResult<List<CatalogDtos.GenreItem>> result = catalogRepository.loadGenres();
            if (result instanceof ApiResult.Success) {
                genres.postValue(((ApiResult.Success<List<CatalogDtos.GenreItem>>) result).getData());
            }
        }).start();
    }

    public void search(String q, String genreId) {
        loading.postValue(true);
        error.postValue(null);
        new Thread(() -> {
            ApiResult<List<CatalogDtos.BookListItem>> result =
                    catalogRepository.loadBooks(q, genreId, "title");
            if (result instanceof ApiResult.Success) {
                books.postValue(((ApiResult.Success<List<CatalogDtos.BookListItem>>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<List<CatalogDtos.BookListItem>>) result).getMessage());
            }
            loading.postValue(false);
        }).start();
    }

    public void loadBookDetail(String id) {
        new Thread(() -> {
            ApiResult<CatalogDtos.BookDetail> result = catalogRepository.loadBook(id);
            if (result instanceof ApiResult.Success) {
                bookDetail.postValue(((ApiResult.Success<CatalogDtos.BookDetail>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<CatalogDtos.BookDetail>) result).getMessage());
            }
        }).start();
    }

    public void clearBookDetail() {
        bookDetail.setValue(null);
    }
}
