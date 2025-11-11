package com.example.mccidanceclub.entities;

import java.time.LocalDate;

public class Events {
    private int eventId;
    private String eventName;
    private LocalDate eventDate;

    // Constructeur vide
    public Events() {}

    // Constructeur sans ID (pour l'ajout)
    public Events(String eventName, LocalDate eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    // Constructeur complet
    public Events(int eventId, String eventName, LocalDate eventDate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    // Getters & Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
}
