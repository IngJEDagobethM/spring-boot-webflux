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
    private final ReactiveMongoTemplate mongoTemplate;
    private final ProductoDao productoDao;

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
    public Mono<ProductoEntity> getProductById(String id) {
        return productoDao.findById(id);
    }

    @Override
    public Mono<Void> deleteProductById(String id) {
        return productoDao.deleteById(id);
    }

    @Override
    public Mono<Void> deleteProduct(ProductoEntity productoEntity) {
        return productoDao.delete(productoEntity);
    }

    @Override
    public Mono<ProductoEntity> getProductByNombre(String nombre) {
        return productoDao.findByNombre(nombre);
    }
}
