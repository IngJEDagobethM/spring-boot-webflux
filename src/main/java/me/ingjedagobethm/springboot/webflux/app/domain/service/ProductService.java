package me.ingjedagobethm.springboot.webflux.app.domain.service;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.repository.ProductRepository;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Validator validator;
    @Value("${config.uploads.path}")
    private String path;

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
        return productRepository.getProductById(id);
    }

    public Mono<ProductoEntity> productById(String id){
        // practicando la transformación de flujos
        Flux<ProductoEntity> products = productRepository.getProducts();
        return products.filter(p -> p.getId().equalsIgnoreCase(id)).next();
    }

    public Mono<Void> deleteProduct(ProductoEntity productoEntity){
        return productRepository.deleteProduct(productoEntity);
    }

    public Mono<ServerResponse> getProductByIdRouter(ServerRequest request){
        return  productRepository.getProductById(request.pathVariable("id"))
                        .flatMap(p -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                // BodyInserters.fromValue() se usa cuando es un objeto no reactivo
                                .body(BodyInserters.fromValue(p))
                        )
                // El ServerResponse.build retorna un Mono
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getProductsRouter(ServerRequest request){
        return  ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productRepository.getProducts(), ProductoEntity.class);
    }

    public Mono<ServerResponse> saveProductRouter(ServerRequest request){
        // Se obtiene el request convertido en Mono de la entidad
        Mono<ProductoEntity> producto = request.bodyToMono(ProductoEntity.class);
        // FlatMap se usa para emitir el producto
        return producto.flatMap(p -> {
            // Manejo de errores
            Errors errors = new BeanPropertyBindingResult(p, ProductoEntity.class.getName()); // Objeto Entidad, Clase
            validator.validate(p, errors);
            if (errors.hasErrors()){
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo ".concat(fieldError.getField()).concat(" ").concat(fieldError.getDefaultMessage()))
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));
            } else {
                if(p.getCreateAt() == null){
                    p.setCreateAt(new Date());
                }
                return productRepository.saveProduct(p)
                        .flatMap((pdb -> ServerResponse
                        .created(URI.create("/api/v2/productos/".concat(pdb.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(pdb)))
                );
            }


        });
    }

    public Mono<ServerResponse> editProductRouter(ServerRequest request){
        // El DTO que viene en la petición
        Mono<ProductoEntity> newProducto = request.bodyToMono(ProductoEntity.class);
        // El PathVariable que viene en la petición
        String id = request.pathVariable("id");

        // El producto consultado por el ID
        Mono<ProductoEntity> producto = productRepository.getProductById(id);

        // Se toma y unifica el flujo del producto en bd con el de la petición
        return producto.zipWith(newProducto, (p, newP) -> {
            p.setNombre(newP.getNombre());
            p.setPrecio(newP.getPrecio());
            p.setCategoria(newP.getCategoria());
            return p;
        }).flatMap((p -> ServerResponse
                .created(URI.create("/api/v2/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productRepository.saveProduct(p), ProductoEntity.class))
        ).switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> deleteProductRouter(ServerRequest request){
        Mono<ProductoEntity> producto = productRepository.getProductById(request.pathVariable("id"));
        return  producto.flatMap(p -> productRepository.deleteProduct(p)
                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> uploadRouter(ServerRequest request){
        String id = request.pathVariable("id");
        // mediante multipartData se obtiene el File
        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                // Se castea a FilePart
                .cast(FilePart.class)
                // Se consulta el Producto por el ID
                .flatMap(filePart -> productRepository.getProductById(id)
                        // Se transforma el flujo para mover la foto al servidor
                        .flatMap(p -> {
                            p.setFoto(UUID.randomUUID().toString().concat(filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", "")
                                    .replace("$", "")));
                            return filePart.transferTo(new File(path.concat(p.getFoto())))
                                    // Se devuelve el Mono de la entidad
                                    .then(productRepository.saveProduct(p));
                        }))
                // Se transforma en un flujo Mono de ServerResponse
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> uploadProductRouter(ServerRequest request){
        Mono<ProductoEntity> producto = request.multipartData().map(multipart -> {
            FormFieldPart nombre = (FormFieldPart) multipart.toSingleValueMap().get("nombre");
            FormFieldPart precio = (FormFieldPart) multipart.toSingleValueMap().get("precio");
            FormFieldPart categoriaId = (FormFieldPart) multipart.toSingleValueMap().get("categoria.id");
            FormFieldPart categoriaNombre = (FormFieldPart) multipart.toSingleValueMap().get("categoria.nombre");

            CategoriaEntity categoria = new CategoriaEntity(categoriaId.value(), categoriaNombre.value());

            return ProductoEntity.builder().nombre(nombre.value()).precio(Double.parseDouble(precio.value())).categoria(categoria).build();
        });
        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                // Se castea a FilePart
                .cast(FilePart.class)
                // Se consulta el Producto por el ID
                .flatMap(filePart -> producto
                        // Se transforma el flujo para mover la foto al servidor
                        .flatMap(p -> {
                            p.setCreateAt(new Date());
                            p.setFoto(UUID.randomUUID().toString().concat(filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", "")
                                    .replace("$", "")));
                            return filePart.transferTo(new File(path.concat(p.getFoto())))
                                    // Se devuelve el Mono de la entidad
                                    .then(productRepository.saveProduct(p));
                        }))
                // Se transforma en un flujo Mono de ServerResponse
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p))
                );
    }

    public Mono<ProductoEntity> getProductByNombre(String nombre){
        return productRepository.getProductByNombre(nombre);
    }
}
