package me.ingjedagobethm.springboot.webflux.app.domain.repository;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository {
    Flux<CategoriaEntity> getCategories();
    Mono<CategoriaEntity> getCategoryById(String id);
    Mono<CategoriaEntity> saveCategory(CategoriaEntity categoriaEntity);
    Mono<Void> dropColection(String colection);
}
