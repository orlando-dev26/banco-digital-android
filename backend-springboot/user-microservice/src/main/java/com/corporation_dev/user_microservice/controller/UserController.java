package com.corporation_dev.user_microservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.corporation_dev.user_microservice.dto.UserDto;
import com.corporation_dev.user_microservice.dto.UserRegistrationDto;
import com.corporation_dev.user_microservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users in the system")
    @GetMapping("/all")
    public Mono<ResponseEntity<Map<String, Object>>> getAllUsers() {
        return this.userService.getAllUsers()
                .collectList()
                .map(users -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("status", true);
                    res.put("users", users);
                    return ResponseEntity.ok(res);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // @GetMapping("all")
    // public Flux<UserDto> getAllUsers() {
    // return this.userService.getAllUsers();
    // }

    @Operation(summary = "Insert a new user", description = "Inserts a new user into the system")
    @PostMapping("/insert")
    public Mono<ResponseEntity<Map<String, Object>>> insertUser(@RequestBody Mono<UserRegistrationDto> registration) {
        return registration
                .flatMap(userService::insertUser)
                .map(userDto -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Usuario registrado exitosamente");
                    response.put("data", userDto);
                    return ResponseEntity.ok(response);
                })
                .onErrorMap(ex -> ex);  // Delega al GlobalExceptionHandler
    }

    // public Mono<ResponseEntity<UserDto>> insertUser(@RequestBody Mono<UserDto>
    // userDto) {
    // return this.userService.insertUser(userDto)
    // .map(ResponseEntity::ok)
    // .defaultIfEmpty(ResponseEntity.notFound().build());
    // }

    @Operation(summary = "Update an existing user", description = "Updates an existing user in the system")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserDto>> updateUser(@PathVariable int id, @RequestBody Mono<UserDto> userDto) {
        // return userService.updateUser(id, userDto);

        return this.userService.updateUser(id, userDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user", description = "Deletes a user from the system by ID")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable int id) {
        return this.userService.deleteUser(id)
                .thenReturn(ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /* Probar el circuit breaker */
    @Operation(summary = "Test circuit breaker", description = "Tests the circuit breaker functionality")
    @GetMapping("/test")
    public Mono<ResponseEntity<String>> testCircuit() {
        return userService.testCircuitBreaker()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(503).build());
    }
}
