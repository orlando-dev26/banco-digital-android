package com.corporation_dev.product_microservice.controller;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corporation_dev.product_microservice.dto.ProductDto;
import com.corporation_dev.product_microservice.event.ProductCreatedEvent;
import com.corporation_dev.product_microservice.event.ProductEventProducer;
import com.corporation_dev.product_microservice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/product")
@Tag(name = "Products API", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService productService;
    private final ProductEventProducer productEventProducer;

    public ProductController(ProductService productService, ProductEventProducer productEventProducer) {
        this.productService = productService;
        this.productEventProducer = productEventProducer;
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products in the system")
    @GetMapping("/all")
    public Mono<ResponseEntity<Map<String, Object>>> getAllProducts() {
        return this.productService.getAllProducts()
            .collectList()
            .map(products -> {
                Map<String, Object> res = new HashMap<>();
                res.put("status", true);
                res.put("products", products);
                return ResponseEntity.ok(res);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get product by Id", description = "Return product find by Id")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getProductById(@PathVariable int id) {
        return this.productService.getProductById(id)
            .map(product -> {
                Map<String, Object> res = new HashMap<>();
                res.put("status", true);
                res.put("product", product);
                return ResponseEntity.ok(res);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/insert")
    public Mono<ResponseEntity<Map<String, Object>>> insertProduct(@RequestBody Mono<ProductDto> productDto) {
        return this.productService.insertProduct(productDto)
            .doOnSuccess(product -> {
                ProductCreatedEvent event = new ProductCreatedEvent(
                    String.valueOf(product.getId()),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice().doubleValue()
                );
                productEventProducer.publishProductCreatedEvent(event);
            })
            .map(product -> {
                Map<String, Object> res = new HashMap<>();
                res.put("status", true);
                res.put("product", product);
                return ResponseEntity.ok(res);
            })
            
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateProductById(@RequestBody Mono<ProductDto> productDto, @PathVariable int id) {
        return this.productService.updateProductById(productDto, id)
            .map(product -> {
                Map<String, Object> res = new HashMap<>();
                res.put("status", true);
                res.put("product", product);
                return ResponseEntity.ok(res);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public Mono<ResponseEntity<String>> testCircuit() {
        return productService.testCircuitBreaker()
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.status(503).build());
    }
    
}
