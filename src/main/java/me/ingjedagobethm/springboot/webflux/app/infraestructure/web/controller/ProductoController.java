package me.ingjedagobethm.springboot.webflux.app.infraestructure.web.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

//@SessionAttributes("producto") // Forma de mantener el Id. Se debe llamar el atributo como se pasa en crearFormulario() y actualizar().
@RequiredArgsConstructor
@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductHandler productHandler;

    @GetMapping({"/productos", "/"})
    public Mono<String> listar(Model model){
        //Flux<ProductoEntity> productos = productHandler.execGetProducts();
        Flux<ProductoEntity> productos = productHandler.execGetProductsUpperCase();

        // El observador se subscribe internamente al trabajar con thymeleaf,
        // pero, es posible subscribir otro observador.
        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado Productos");
        return Mono.just("listar");
    }

    @GetMapping("/productos/datadriver")
    public Mono<String> listarDataDriver(Model model){
        Flux<ProductoEntity> productos = productHandler.execGetProducts()
                .delayElements(Duration.ofSeconds(2));
        // new ReactiveDataDriverContextVariable(productos, 1) es como se maneja la contrapresión (flujo, elementos_a_mostrar)
        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado Productos (DataDriver)");
        return Mono.just("listar");
    }

    @GetMapping("/productos/chunked")
    public Mono<String> listarChunked(Model model){
        Flux<ProductoEntity> productos = productHandler.execGetProductsMasive();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado Productos (Chunked)");
        return Mono.just("listar");
    }

    @GetMapping("/productos/")
    public Mono<String> crearFormulario(Model model){
        model.addAttribute("producto", new ProductoEntity());
        model.addAttribute("titulo", "Nuevo Producto");
        return Mono.just("formulario");
    }

    @PostMapping("/productos/")
    public Mono<String> insertar(ProductoEntity producto/*, SessionStatus status*/){
        //status.setComplete();
        return productHandler.execSaveProduct(producto)
                .doOnNext(p -> log.info("Producto <".concat(producto.getId()).concat(" ").concat(p.getNombre()).concat("> insertado.")))
                .thenReturn("redirect:/productos");
        //      .then(Mono.just("redirect:/productos"));
    }

    @GetMapping("/productos/{id}")
    public Mono<String> actualizar(@PathVariable String id, Model model){
        Mono<ProductoEntity> producto = productHandler.execGetById(id)
                .doOnNext(p -> log.info("Producto <".concat(p.getId()).concat(" ").concat(p.getNombre()).concat("> preparado para modificar.")))
                .defaultIfEmpty(new ProductoEntity()); // Para que al fallar devuelva la instancia de Producto vacia y no error.

        model.addAttribute("producto", producto);
        model.addAttribute("titulo", "Actualizar Producto");

        return Mono.just("formulario");
    }
}
