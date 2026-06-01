package com.example.applibrary.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.applibrary.di.AppContainer;
import com.example.applibrary.ui.catalog.CatalogViewModel;
import com.example.applibrary.ui.events.EventsViewModel;
import com.example.applibrary.ui.home.HomeViewModel;
import com.example.applibrary.ui.login.LoginViewModel;
import com.example.applibrary.ui.loans.LoansViewModel;
import com.example.applibrary.ui.profile.ProfileViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final AppContainer container;

    public ViewModelFactory(@NonNull Application application, @NonNull AppContainer container) {
        this.application = application;
        this.container = container;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(application);
        }
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(application, container.getDashboardRepository());
        }
        if (modelClass.isAssignableFrom(CatalogViewModel.class)) {
            return (T) new CatalogViewModel(container.getCatalogRepository());
        }
        if (modelClass.isAssignableFrom(LoansViewModel.class)) {
            return (T) new LoansViewModel(container.getLoanRepository());
        }
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(container.getProfileRepository());
        }
        if (modelClass.isAssignableFrom(EventsViewModel.class)) {
            return (T) new EventsViewModel(container.getEventRepository());
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
