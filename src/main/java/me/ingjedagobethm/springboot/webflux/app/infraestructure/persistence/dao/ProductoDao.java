package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductoDao extends ReactiveMongoRepository<ProductoEntity, String> {
    Mono<ProductoEntity> findByNombre(String nombre);
    @Query("{ 'nombre': ?0 }") // 1?, 2?, 3?, n? para cada uno de los parámetros
    Mono<ProductoEntity> buscarPorNombre(String nombre);
}
