package com.corporation_dev.product_microservice.repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import com.corporation_dev.product_microservice.entity.Product;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Integer> {

}
