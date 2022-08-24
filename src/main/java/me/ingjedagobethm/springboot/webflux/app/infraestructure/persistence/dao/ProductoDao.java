package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDao extends ReactiveMongoRepository<ProductoEntity, String> {
}
