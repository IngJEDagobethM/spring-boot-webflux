package me.ingjedagobethm.springboot.webflux.app.domain.service;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.repository.ProductRepository;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Mono<ProductoEntity> saveProduct(ProductoEntity productoEntity){
        return productRepository.saveProduct(productoEntity);
    }

    public Mono<Void> dropCollection(String colection){
        return  productRepository.dropColection(colection);
    }

    public Flux<ProductoEntity> getProducts(){
        return productRepository.getProducts();
    }

    public Flux<ProductoEntity> getProductsMasive(){
        return productRepository.getProducts()
                .repeat(200);
    }

    public Flux<ProductoEntity> getProductsUpperCase(){
        return productRepository.getProducts().map(product -> {
            product.setNombre(product.getNombre().toUpperCase());
            return product;
        });
    }

    public Mono<ProductoEntity> getById(String id){
        return productRepository.productById(id);
    }

    public Mono<ProductoEntity> productById(String id){
        // practicando la transformación de flujos
        Flux<ProductoEntity> products = productRepository.getProducts();
        return products.filter(p -> p.getId().equalsIgnoreCase(id)).next();
    }
}
