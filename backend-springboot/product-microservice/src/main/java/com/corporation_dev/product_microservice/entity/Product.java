package com.corporation_dev.product_microservice.entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table("products")
public class Product {
    @Id
    @Column("id")
    private int id;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
    @Column("price")
    private BigDecimal price;
    @Column("stock")
    private int stock;
    @Column("status")
    private String status;
    @Column("created_at")
    private LocalDate created_at;
    @Column("updated_at")
    private LocalDate updated_at;
}
