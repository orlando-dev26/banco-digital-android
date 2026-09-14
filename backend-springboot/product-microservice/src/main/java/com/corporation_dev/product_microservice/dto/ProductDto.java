package com.corporation_dev.product_microservice.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductDto {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String status;
    private LocalDate created_at;
    private LocalDate updated_at;
}
