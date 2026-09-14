package com.corporation_dev.user_microservice.service;

import org.springframework.stereotype.Service;
import com.corporation_dev.user_microservice.dto.UserDto;
import com.corporation_dev.user_microservice.dto.UserRegistrationDto;
import com.corporation_dev.user_microservice.entity.User;
import com.corporation_dev.user_microservice.exception.UserAlreadyExistsException;
import com.corporation_dev.user_microservice.repository.UserRepository;
import com.corporation_dev.user_microservice.util.EntityDtoUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final Auth0Service auth0Service;

    public UserService(UserRepository userRepository, WebClient webClient, Auth0Service auth0Service) {
        this.userRepository = userRepository;
        this.webClient = webClient;
        this.auth0Service = auth0Service;
    }

    public Flux<UserDto> getAllUsers() {
        return this.userRepository.findAll()
                .map(EntityDtoUtil::toDto);
    }

    /* Insertar un nuevo usuario */
    @CircuitBreaker(name = "insertUserCircuitBreaker", fallbackMethod = "insertUserFallback")
    public Mono<UserDto> insertUser(UserRegistrationDto registration) {

        return auth0Service.createUser(
                registration.getDni(),
                registration.getPassword(),
                registration.getEmail(),
                registration.getName(),
                registration.getLastName())
                .flatMap(auth0Sub -> {

                    User user = new User();

                    user.setDni(registration.getDni());
                    user.setAuth0Sub(auth0Sub);
                    user.setCelular(registration.getCelular());
                    user.setEmail(registration.getEmail());
                    user.setName(registration.getName());
                    user.setLastName(registration.getLastName());
                    user.setRole("USER");
                    user.setStatus("ACTIVE");

                    return userRepository.save(user)
                            .onErrorMap(ex -> {
                                // Manejo de errores de base de datos (duplicado de DNI, email, etc)
                                if (ex.getMessage().contains("Duplicate") || 
                                    ex.getMessage().contains("duplicate") ||
                                    ex.getMessage().contains("UNIQUE")) {
                                    return new UserAlreadyExistsException(
                                            "El usuario con DNI o email ya existe en la base de datos");
                                }
                                return ex;
                            });
                })
                .map(EntityDtoUtil::toDto);
    }

    /* Fallback insertUser */
    public Mono<UserDto> insertUserFallback(UserRegistrationDto registration, Throwable ex) {

        System.out.println("Fallback insertUser - Error: " + ex.getMessage());
        ex.printStackTrace();

        if (ex instanceof UserAlreadyExistsException) {
            return Mono.error(ex);
        }

        // Si es otro tipo de error, envolver en RuntimeException
        return Mono.error(new RuntimeException("Servicio de usuarios no disponible temporalmente: " + ex.getMessage(), ex));
    }

    /* Actualizar un usuario existente */
    public Mono<UserDto> updateUser(int id, Mono<UserDto> userDto) {
        return userRepository.findById(id)
                .flatMap(existingUser -> userDto.map(EntityDtoUtil::toEntity))
                .doOnNext(newUser -> newUser.setId(id))
                .flatMap(userRepository::save)
                .map(EntityDtoUtil::toDto);
    }

    /* Eliminar usuario por ID */
    public Mono<Void> deleteUser(int id) {
        return userRepository.deleteById(id);
    }

    /* Probar el circuit breaker */
    @CircuitBreaker(name = "testCircuitBreaker", fallbackMethod = "testCircuitBreakerFallback")
    public Mono<String> testCircuitBreaker() {
        return webClient
                .get()
                .uri("http://www.googleerror.com")
                .retrieve()
                .bodyToMono(String.class)
                .map(res -> "Operación exitosa")
                .doOnError(ex -> System.out.println("-> Error capturado: " + ex.getClass().getSimpleName()))
                .onErrorMap(ex -> new RuntimeException("Servicio de prueba no disponible"));
    }

    public Mono<String> testCircuitBreakerFallback(Throwable ex) {
        System.out.println("-> FALLBACK ejecutado: Servicio temporalmente no disponible");
        return Mono.just("Fallback: Servicio no disponible");
    }
}
