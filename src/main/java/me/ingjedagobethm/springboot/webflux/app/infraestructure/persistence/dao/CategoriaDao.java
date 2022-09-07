package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoriaDao extends ReactiveMongoRepository<CategoriaEntity,String> {
    Mono<CategoriaEntity> findByNombre(String nombre);
}
