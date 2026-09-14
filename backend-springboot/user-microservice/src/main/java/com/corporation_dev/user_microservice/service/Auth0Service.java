package com.corporation_dev.user_microservice.service;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.ParameterizedTypeReference;

import com.corporation_dev.user_microservice.exception.UserAlreadyExistsException;
import com.corporation_dev.user_microservice.exception.ValidationException;

import java.util.Map;
import java.util.HashMap;

import reactor.core.publisher.Mono;

@Service
public class Auth0Service {

    private final WebClient webClient;

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.connection}")
    private String connection;

    public Auth0Service(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene un Access Token para utilizar la Auth0 Management API.
     */
    private Mono<String> getManagementToken() {

        return webClient
                .post()
                .uri("https://" + domain + "/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=client_credentials"
                                + "&client_id=" + clientId
                                + "&client_secret=" + clientSecret
                                + "&audience=" + audience)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                .map(response -> (String) response.get("access_token"));
    }

    /**
     * Crea un usuario en Auth0.
     *
     * @return el user_id generado por Auth0.
     */
    public Mono<String> createUser(
            String dni,
            String password,
            String email,
            String name,
            String lastName) {

        return getManagementToken()
                .flatMap(token -> {

                    if (token == null || token.isBlank()) {
                        return Mono.error(
                                new IllegalStateException(
                                        "Auth0 no devolvió un access token"));
                    }

                    Map<String, Object> user = new HashMap<>();

                    user.put("connection", connection);
                    user.put("username", dni);
                    user.put("password", password);
                    user.put("email", email);
                    user.put("given_name", name);
                    user.put("family_name", lastName);
                    user.put("name", name + " " + lastName);

                    return webClient
                            .post()
                            .uri("https://" + domain + "/api/v2/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(user)
                            .retrieve()

                            // Manejo específico del usuario duplicado (409)
                            .onStatus(
                                    status -> status.value() == 409,
                                    response -> Mono.error(
                                            new UserAlreadyExistsException(
                                                    "El usuario ya existe en Auth0")))

                            // Manejo específico de errores de validación (400)
                            .onStatus(
                                    status -> status.value() == 400,
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                String errorMessage = extractAuth0ErrorMessage(body);
                                                return Mono.error(
                                                        new ValidationException(errorMessage));
                                            }))

                            .bodyToMono(
                                    new ParameterizedTypeReference<Map<String, Object>>() {
                                    })

                            .map(response -> {

                                Object userId = response.get("user_id");

                                if (userId == null) {
                                    throw new IllegalStateException(
                                            "Auth0 no devolvió user_id");
                                }

                                return userId.toString();
                            })
                            
                            // Captura de otros errores no manejados
                            .onErrorMap(ex -> {
                                if (ex instanceof UserAlreadyExistsException || 
                                    ex instanceof ValidationException) {
                                    return ex;
                                }
                                return new RuntimeException("Error al comunicarse con Auth0: " + ex.getMessage(), ex);
                            });
                });
    }

    /**
     * Extrae el mensaje de error de la respuesta de Auth0
     */
    private String extractAuth0ErrorMessage(String responseBody) {
        try {
            // Intenta parsear como JSON para extraer el mensaje de error
            if (responseBody != null && responseBody.contains("statusCode")) {
                // Error JSON de Auth0
                if (responseBody.contains("\"description\"")) {
                    int descStart = responseBody.indexOf("\"description\":");
                    int colonPos = responseBody.indexOf(":", descStart);
                    int firstQuote = responseBody.indexOf("\"", colonPos);
                    int secondQuote = responseBody.indexOf("\"", firstQuote + 1);
                    
                    if (firstQuote > 0 && secondQuote > firstQuote) {
                        return responseBody.substring(firstQuote + 1, secondQuote);
                    }
                }
                // Si tiene statusCode pero sin description
                if (responseBody.contains("\"message\"")) {
                    int msgStart = responseBody.indexOf("\"message\":");
                    int colonPos = responseBody.indexOf(":", msgStart);
                    int firstQuote = responseBody.indexOf("\"", colonPos);
                    int secondQuote = responseBody.indexOf("\"", firstQuote + 1);
                    
                    if (firstQuote > 0 && secondQuote > firstQuote) {
                        return responseBody.substring(firstQuote + 1, secondQuote);
                    }
                }
            }
        } catch (Exception e) {
            // Si no se puede parsear, usar el mensaje genérico
        }
        
        // Mensaje por defecto si no se puede extraer el específico
        if (responseBody != null && responseBody.contains("weak")) {
            return "La contraseña no cumple con los requisitos de seguridad. Debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y caracteres especiales.";
        }
        if (responseBody != null && responseBody.contains("already")) {
            return "El usuario ya existe en Auth0";
        }
        
        return "Error de validación en Auth0: " + (responseBody != null ? responseBody : "Error desconocido");
    }
}