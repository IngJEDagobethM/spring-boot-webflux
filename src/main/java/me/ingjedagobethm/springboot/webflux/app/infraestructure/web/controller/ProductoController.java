package me.ingjedagobethm.springboot.webflux.app.infraestructure.web.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository.ProductoRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequiredArgsConstructor
@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductHandler productHandler;

    @GetMapping({"/productos", "/"})
    public String listar(Model model){
        //Flux<ProductoEntity> productos = productHandler.execGetProducts();
        Flux<ProductoEntity> productos = productHandler.execGetProductsUpperCase();

        // El observador se subscribe internamente al trabajar con thymeleaf,
        // pero, es posible subscribir otro observador.
        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado Productos");
        return "listar";
    }

    @GetMapping("/productos/datadriver")
    public String listarDataDriver(Model model){
        Flux<ProductoEntity> productos = productHandler.execGetProducts()
                .delayElements(Duration.ofSeconds(2));
        // new ReactiveDataDriverContextVariable(productos, 1) es como se maneja la contrapresión (flujo, elementos_a_mostrar)
        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado Productos (DataDriver)");
        return "listar";
    }

    @GetMapping("/productos/chunked")
    public String listarChunked(Model model){
        Flux<ProductoEntity> productos = productHandler.execGetProductsMasive();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado Productos (Chunked)");
        return "listar";
    }
}
