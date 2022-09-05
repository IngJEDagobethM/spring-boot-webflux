package me.ingjedagobethm.springboot.webflux.app.infraestructure.api.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping(value = "/api/productos")
@RestController
public class ProductoRestController {

    private static final Logger log = LoggerFactory.getLogger(ProductoRestController.class);
    private final ProductHandler productHandler;
    @Value("${config.uploads.path}")
    private String path;

    @GetMapping
    public Mono<ResponseEntity<Flux<ProductoEntity>>> listarTodo(){
        return Mono.just(
                ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productHandler.execGetProducts())
        );
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<ProductoEntity>> listarPorIdentificador(@PathVariable String id){
        /*
        return Mono.just(
                ResponseEntity.ok(productHandler.execGetById(id))
        );
        */
        return productHandler.execGetById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> crearProducto(@Valid @RequestBody Mono<ProductoEntity> productoEntity){

        Map<String, Object> response = new HashMap<>();

        return productoEntity.flatMap(producto -> {
            if (producto.getCreateAt() == null) {
                producto.setCreateAt(new Date());
            }
            return productHandler.execSaveProduct(producto).map(p -> {
                response.put("status", HttpStatus.CREATED);
                response.put("mensaje", "Producto creado con éxito.");
                response.put("timestamp", new Date());
                response.put("producto", p);
                return ResponseEntity
                        .created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            });
            // ERROR: Se obtiene un throwable
        }).onErrorResume(t -> {
            // Se transforma al tipo específico
            return Mono.just(t).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    // La lista es transformada en un Flux
                    .flatMapMany(Flux::fromIterable)
                    // El flujo de FieldError se simplifica en una Lista de String
                    .map(fieldError -> "El campo ".concat(fieldError.getField().concat(" ")
                            .concat(Objects.requireNonNull(fieldError.getDefaultMessage()))))
                    .collectList()
                    .flatMap(list -> {
                        response.put("status", HttpStatus.BAD_REQUEST.value());
                        response.put("timestamp", new Date());
                        response.put("errors", list);
                        return Mono.just(ResponseEntity.badRequest().body(response));
                    });
        });

    }

    @PutMapping(value = "/{id}")
    public Mono<ResponseEntity<ProductoEntity>> actualizarProducto(@RequestBody ProductoEntity productoEntity, @PathVariable String id){
        return productHandler.execProductById(id).flatMap(p -> {
            p.setNombre(productoEntity.getNombre());
            p.setPrecio(productoEntity.getPrecio());
            p.setCategoria(productoEntity.getCategoria());
           return productHandler.execSaveProduct(p);
        }).map(p -> ResponseEntity.created(URI
                        .create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> eliminarProducto(@PathVariable String id){
        return productHandler.execGetById(id)
                .flatMap(p -> {
                    return productHandler.execDeleteProduct(p)
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<ProductoEntity>> cargarFoto(@PathVariable String id, @RequestPart FilePart file){
        return productHandler.execGetById(id)
                .flatMap(p -> {
                    p.setFoto(UUID.randomUUID().toString().concat(file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", "")
                            .replace("$", "")));
                    return file.transferTo(new File(path.concat(p.getFoto())))
                            .then(productHandler.execSaveProduct(p));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/v2")
    public Mono<ResponseEntity<ProductoEntity>> crearProductoConFoto(ProductoEntity productoEntity, @RequestPart FilePart file){
        if (productoEntity.getCreateAt() == null){
            productoEntity.setCreateAt(new Date());
        }
        productoEntity.setFoto(UUID.randomUUID().toString().concat(file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", "")
                .replace("$", "")));
        // Se mueve el archivo a la ruta de cargue
        return file.transferTo(new File(path.concat(productoEntity.getFoto())))
                // Se guarda el producto
                .then(productHandler.execSaveProduct(productoEntity))
                // Se itera sobre el objeto regresado para armar la respuesta
                .map(p -> ResponseEntity
                        .created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p));
    }
}
