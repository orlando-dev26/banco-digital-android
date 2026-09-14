package com.corporation_dev.product_microservice.service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.corporation_dev.product_microservice.dto.ProductDto;
import com.corporation_dev.product_microservice.repository.ProductRepository;
import com.corporation_dev.product_microservice.util.EntityDtoUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WebClient webClient;

    public ProductService(ProductRepository productRepository, WebClient webClient) {
        this.productRepository = productRepository;
        this.webClient = webClient;
    }

    /* Obtener todos los productos */
    @CircuitBreaker(name = "getAllProductsCircuitBreaker", fallbackMethod = "getAllProductsFallback")
    public Flux<ProductDto> getAllProducts() {
        return this.productRepository.findAll()
            // .doOnNext(p -> System.out.println("PRODUCTO: " + p))
            .map(EntityDtoUtil::toDto);
    }

    public Flux<String> getAllProductsFallback(Throwable ex) {
        System.out.println("Fallback - Error REAL: " + ex.getMessage());
        return Flux.just("Fallback: Servicio no disponible");
    }

    /* Obtener producto por id */
    @CircuitBreaker(name = "getProductByIdCircuitBreaker", fallbackMethod = "getProductByIdFallback")
    public Mono<ProductDto> getProductById(int id) {
        return this.productRepository.findById(id)
            .map(EntityDtoUtil::toDto);
    }

    public Mono<String> getProductByIdFallback(Throwable ex) {
        System.out.println("Fallback - Error REAL: " + ex.getMessage());
        return Mono.just("Fallback: Servicio no disponible");
    }

    /* Insertar producto */
    @CircuitBreaker(name = "insertProductCircuitBreaker", fallbackMethod = "insertProductFallback")
    public Mono<ProductDto> insertProduct(Mono<ProductDto> productDto) {
        return productDto.map(EntityDtoUtil::toEntity)
            .flatMap(productRepository::save)
            .map(EntityDtoUtil::toDto);
    }

    public Mono<String> insertProductFallback(Throwable ex) {
        System.out.println("Fallback - Error REAL: " + ex.getMessage());
        return Mono.just("Fallback: Servicio no disponible");
    }

    /* Actualizar producto */
    @CircuitBreaker(name = "updateProductByIdCircuitBreaker", fallbackMethod = "updateProductByIdFallback")
    public Mono<ProductDto> updateProductById(Mono<ProductDto> productDto, int id) {
        return productDto
            .map(EntityDtoUtil::toEntity)
            .doOnNext(product -> product.setId(id))
            .flatMap(productRepository::save)
            .map(EntityDtoUtil::toDto);
    }

    public Mono<String> updateProductByIdFallback(Throwable ex) {
        System.out.println("Fallback - Error REAL: " + ex.getMessage());
        return Mono.just("Fallback: Servicio no disponible");
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
