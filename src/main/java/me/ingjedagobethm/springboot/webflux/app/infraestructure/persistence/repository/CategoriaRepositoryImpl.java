package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.repository.CategoryRepository;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao.CategoriaDao;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class CategoriaRepositoryImpl  implements CategoryRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final CategoriaDao categoriaDao;

    @Override
    public Flux<CategoriaEntity> getCategories() {
        return categoriaDao.findAll();
    }

    @Override
    public Mono<CategoriaEntity> getCategoryById(String id) {
        return categoriaDao.findById(id);
    }

    @Override
    public Mono<CategoriaEntity> saveCategory(CategoriaEntity categoriaEntity) {
        return categoriaDao.save(categoriaEntity);
    }

    @Override
    public Mono<Void> dropColection(String colection) {
        return mongoTemplate.dropCollection(colection);
    }

    @Override
    public Mono<CategoriaEntity> getCategoryByNombre(String nombre) {
        return categoriaDao.findByNombre(nombre);
    }
}
