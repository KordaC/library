package com.example.applibrary.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.EventDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

public class EventsViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<EventDtos.EventItem>> events = new MutableLiveData<>(new ArrayList<>());

    public EventsViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<List<EventDtos.EventItem>> getEvents() { return events; }

    public void load() {
        loading.setValue(true);
        new Thread(() -> {
            ApiResult<List<EventDtos.EventItem>> result = eventRepository.listEvents();
            if (result instanceof ApiResult.Success) {
                events.postValue(((ApiResult.Success<List<EventDtos.EventItem>>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<List<EventDtos.EventItem>>) result).getMessage());
            }
            loading.postValue(false);
        }).start();
    }

    public void toggleRegistration(EventDtos.EventItem item) {
        new Thread(() -> {
            ApiResult<?> result = item.registeredByMe
                    ? eventRepository.unregister(item.id)
                    : eventRepository.register(item.id);
            if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<?>) result).getMessage());
            } else {
                load();
            }
        }).start();
    }
}
