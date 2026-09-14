package com.corporation_dev.product_microservice.util;
import java.time.LocalDate;
import org.springframework.beans.BeanUtils;
import com.corporation_dev.product_microservice.dto.ProductDto;
import com.corporation_dev.product_microservice.entity.Product;

public class EntityDtoUtil {
    public static ProductDto toDto(Product product) {
        ProductDto userDto = new ProductDto();
        BeanUtils.copyProperties(product, userDto);
        return userDto;
    }

    public static Product toEntity(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        if (product.getCreated_at() == null) {
            product.setCreated_at(LocalDate.now());
        }
        if (product.getUpdated_at() == null) {
            product.setUpdated_at(LocalDate.now());
        }
        return product;
    }
}
