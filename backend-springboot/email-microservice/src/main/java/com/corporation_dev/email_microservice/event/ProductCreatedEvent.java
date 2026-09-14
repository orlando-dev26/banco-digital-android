package com.corporation_dev.email_microservice.event;

public record ProductCreatedEvent(String id, String name, String description, Double price)
{ }
