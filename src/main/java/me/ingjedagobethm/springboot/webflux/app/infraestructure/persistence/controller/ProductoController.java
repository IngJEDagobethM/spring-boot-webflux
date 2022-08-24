package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoRepository productoRepository;

    @GetMapping({"/productos", "/"})
    public String listar(Model model){
        //Flux<ProductoEntity> productos = productoRepository.listarProductos();
        Flux<ProductoEntity> productos = productoRepository.listarProductosNombreUpperCase();

        // El observador se subscribe internamente al trabajar con thymeleaf,
        // pero, es posible subscribir otro observador.
        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado Productos");
        return "listar";
    }
}
