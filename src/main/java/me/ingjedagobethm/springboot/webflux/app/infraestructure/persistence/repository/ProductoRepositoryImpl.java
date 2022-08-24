package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.repository.ProductRepository;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao.ProductoDao;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class ProductoRepositoryImpl implements ProductRepository {
    private final ProductoDao productoDao;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ProductoEntity> saveProduct(ProductoEntity productoEntity) {
        return productoDao.save(productoEntity);
    }

    @Override
    public Mono<Void> dropColection(String colection) {
        return mongoTemplate.dropCollection(colection);
    }

    @Override
    public Flux<ProductoEntity> getProducts() {
        return productoDao.findAll();
    }

    @Override
    public Mono<ProductoEntity> productById(String id) {
        return productoDao.findById(id);
    }
}
