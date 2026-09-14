package com.corporation_dev.user_microservice.dto;

import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@ToString
public class UserDto {
    private int id;
    private String dni;
    private String auth0Sub;
    private String celular;
    private String name;
    private String lastName;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
