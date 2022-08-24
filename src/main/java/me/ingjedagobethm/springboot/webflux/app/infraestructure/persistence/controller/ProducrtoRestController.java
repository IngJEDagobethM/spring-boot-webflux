package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping(value = "/api/productos")
@RestController
public class ProducrtoRestController {
    private static final Logger log = LoggerFactory.getLogger(ProducrtoRestController.class);
    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<Flux<ProductoEntity>> listarTodo(){
        return new ResponseEntity<>(productoRepository.listarProductos(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Mono<ProductoEntity>> listarPorIdentificador(@PathVariable String id){
        return new ResponseEntity<>(productoRepository.productoPorIdentificador(id), HttpStatus.OK);
    }
}
