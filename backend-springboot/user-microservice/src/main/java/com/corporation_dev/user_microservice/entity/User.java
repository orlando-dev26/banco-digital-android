package com.corporation_dev.user_microservice.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@ToString
@Table("users")
public class User {

    @Id
    @Column("ID")
    private int id;

    @Column("DNI")
    private String dni;

    @Column("AUTH0_SUB")
    private String auth0Sub;

    @Column("CELULAR")
    private String celular;

    @Column("NAME")
    private String name;

    @Column("LAST_NAME")
    private String lastName;

    @Column("EMAIL")
    private String email;

    @Column("ROLE")
    private String role;

    @Column("STATUS")
    private String status;

    @Column("CREATED_AT")
    private LocalDateTime createdAt;

    @Column("UPDATED_AT")
    private LocalDateTime updatedAt;
}
