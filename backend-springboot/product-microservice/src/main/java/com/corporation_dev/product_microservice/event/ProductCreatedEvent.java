package com.corporation_dev.product_microservice.event;

public record ProductCreatedEvent(String id, String name, String description, double price) 
{ }
