package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao.ProductoDao;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class ProductoRepository {
    private final ProductoDao productoDao;

    public Mono<ProductoEntity> insertarProducto(ProductoEntity productoEntity){
        return productoDao.save(productoEntity);
    }
}
