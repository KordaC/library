package com.example.library.repository;

import com.example.library.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByStartsAtAfterOrderByStartsAtAsc(LocalDateTime after);
}
