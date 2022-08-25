package me.ingjedagobethm.springboot.webflux.app.application.handler;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.service.ProductService;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductHandler {

    private final ProductService productService;

    public Mono<ProductoEntity> execSaveProduct(ProductoEntity productoEntity){
        return productService.saveProduct(productoEntity);
    }

    public Mono<Void> execDropCollection(String colection){
        return  productService.dropCollection(colection);
    }

    public Flux<ProductoEntity> execGetProducts(){
        return productService.getProducts();
    }

    public Flux<ProductoEntity> execGetProductsMasive(){
        return productService.getProducts()
                .repeat(5000);
    }

    public Flux<ProductoEntity> execGetProductsUpperCase(){
        return productService.getProducts().map(product -> {
            product.setNombre(product.getNombre().toUpperCase());
            return product;
        });
    }

    public Mono<ProductoEntity> execGetById(String id){
        return productService.productById(id);
    }

    public Mono<ProductoEntity> execProductById(String id){
        // practicando la transformación de flujos
        Flux<ProductoEntity> products = productService.getProducts();
        return products.filter(p -> p.getId().equalsIgnoreCase(id)).next();
    }

    public Mono<Void> execDeleteProduct(ProductoEntity productoEntity){
        return productService.deleteProduct(productoEntity);
    }
}
