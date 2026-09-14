package com.corporation_dev.email_microservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.corporation_dev.email_microservice.event.ProductCreatedEvent;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(ProductCreatedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("louis.dev94@gmail.com");
        message.setSubject("Nuevo producto registrado!");
        message.setText("Se ha creado el producto; " + event.name() + " con precio: " + event.price());
        mailSender.send(message);
    }
}
