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

    public Flux<ProductoEntity> listarProductosRepetidos(){
        return productoDao.findAll()
                .repeat(200);
    }

    public Flux<ProductoEntity> listarProductosNombreUpperCase(){
        return productoDao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    public Mono<ProductoEntity> productoPorIdentificador(String id){
        // la forma optima de regresar una búsqueda por identificador
        //return productoDao.findById(id);

        // practicando la transformación de flujos
        Flux<ProductoEntity> productos = productoDao.findAll();
        return productos.filter(p -> p.getId().equalsIgnoreCase(id)).next();
    }
}
