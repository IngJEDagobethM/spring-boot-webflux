package me.ingjedagobethm.springboot.webflux.app.domain.repository;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<ProductoEntity> saveProduct(ProductoEntity productoEntity);
    Mono<Void> dropColection(String colection);
    Flux<ProductoEntity> getProducts();
    Mono<ProductoEntity> productById(String id);
}
