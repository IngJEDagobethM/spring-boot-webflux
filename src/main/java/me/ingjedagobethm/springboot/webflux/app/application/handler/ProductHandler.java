package me.ingjedagobethm.springboot.webflux.app.application.handler;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.service.ProductService;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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

    public Mono<ServerResponse> execGetProductsRouter(ServerRequest request){
        return productService.getProductsRouter(request);
    }

    public Mono<ServerResponse> execGetProductByIdRouter(ServerRequest request){
        return productService.getProductByIdRouter(request);
    }

    public Mono<ServerResponse> execSaveProductRouter(ServerRequest request){
        return productService.saveProductRouter(request);
    }

    public Mono<ServerResponse> execEditProductRouter(ServerRequest request){
        return productService.editProductRouter(request);
    }

    public Mono<ServerResponse> execDeleteProductRouter(ServerRequest request){
        return productService.deleteProductRouter(request);
    }

    public Mono<ServerResponse> execUploadRouter(ServerRequest request){
        return productService.uploadRouter(request);
    }

    public Mono<ServerResponse> execUploadProductRouter(ServerRequest request){
        return productService.uploadProductRouter(request);
    }

    public Mono<ProductoEntity> execFindByNombre(String nombre){
        return productService.getProductByNombre(nombre);
    }
}
