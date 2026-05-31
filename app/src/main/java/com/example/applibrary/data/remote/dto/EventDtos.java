package com.example.applibrary.data.remote.dto;

public class EventDtos {

    public static class EventItem {
        public String id;
        public String title;
        public String type;
        public String description;
        public String startsAt;
        public int capacity;
        public int registeredCount;
        public boolean registeredByMe;
    }
}
