package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.dao.ProductoDao;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class ProductoRepository {
    private final ProductoDao productoDao;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ProductoEntity> insertarProducto(ProductoEntity productoEntity){
        return productoDao.save(productoEntity);
    }

    public Mono<Void> eliminarColeccion(String coleccion){
        return  mongoTemplate.dropCollection(coleccion);
    }

    public Flux<ProductoEntity> listarProductos(){
        return productoDao.findAll();
    }

    public Flux<ProductoEntity> listarProductosNombreUpperCase(){
        return listarProductos().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }
}
