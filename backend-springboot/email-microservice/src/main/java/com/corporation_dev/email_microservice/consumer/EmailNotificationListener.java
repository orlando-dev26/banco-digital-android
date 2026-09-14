package com.corporation_dev.email_microservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.corporation_dev.email_microservice.event.ProductCreatedEvent;
import com.corporation_dev.email_microservice.service.EmailService;

@Component
public class EmailNotificationListener {
    private final EmailService emailService;

    public EmailNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "products-topic", groupId = "email-service")
    public void handleProductCreated(ProductCreatedEvent event) {
        System.out.println("========================================");
        System.out.println("=========== EVENTO DETECTADO ===========");
        System.out.println(event);

        emailService.sendNotification(event);
        System.out.println("=========== CORREO ENVIADO! ============");
    }
}
