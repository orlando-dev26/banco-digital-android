package com.corporation_dev.user_microservice.dto;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegistrationDto {
    private String dni;
    private String password;
    private String celular;
    private String email;
    private String name;
    private String lastName;
}
