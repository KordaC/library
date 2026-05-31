package com.example.library.service;

import com.example.library.dto.EventDtos;
import com.example.library.entity.Event;
import com.example.library.entity.EventRegistration;
import com.example.library.exception.ApiException;
import com.example.library.repository.EventRegistrationRepository;
import com.example.library.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, EventRegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public List<EventDtos.EventItem> listEvents(UUID userId) {
        return eventRepository.findByStartsAtAfterOrderByStartsAtAsc(LocalDateTime.now().minusHours(1))
                .stream()
                .map(e -> toItem(e, userId))
                .toList();
    }

    @Transactional
    public EventDtos.RegisterResponse register(UUID userId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Мероприятие не найдено", HttpStatus.NOT_FOUND));

        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ApiException("ALREADY_REGISTERED", "Вы уже записаны", HttpStatus.CONFLICT);
        }

        long count = registrationRepository.countByEventId(eventId);
        if (count >= event.getCapacity()) {
            throw new ApiException("EVENT_FULL", "Мест нет", HttpStatus.CONFLICT);
        }

        EventRegistration reg = new EventRegistration();
        reg.setId(UUID.randomUUID());
        reg.setEventId(eventId);
        reg.setUserId(userId);
        reg.setRegisteredAt(LocalDateTime.now());
        registrationRepository.save(reg);

        return new EventDtos.RegisterResponse(true);
    }

    @Transactional
    public void unregister(UUID userId, UUID eventId) {
        EventRegistration reg = registrationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ApiException("NOT_REGISTERED", "Запись не найдена", HttpStatus.NOT_FOUND));
        registrationRepository.delete(reg);
    }

    private EventDtos.EventItem toItem(Event e, UUID userId) {
        long registered = registrationRepository.countByEventId(e.getId());
        boolean mine = registrationRepository.existsByEventIdAndUserId(e.getId(), userId);
        return new EventDtos.EventItem(
                e.getId().toString(),
                e.getTitle(),
                e.getType(),
                e.getDescription(),
                e.getStartsAt().toString(),
                e.getCapacity(),
                (int) registered,
                mine
        );
    }
}
