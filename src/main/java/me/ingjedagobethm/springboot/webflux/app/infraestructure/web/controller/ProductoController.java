package me.ingjedagobethm.springboot.webflux.app.infraestructure.web.controller;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.CategoryHandler;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

//@SessionAttributes("producto") // Forma de mantener el Id. Se debe llamar el atributo como se pasa en crearFormulario() y actualizar().
@RequiredArgsConstructor
@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductHandler productHandler;
    private final CategoryHandler categoryHandler;
    @Value("${config.uploads.path}")
    private String uploadPath = "";

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
    public Mono<String> insertar(
            @Valid @ModelAttribute("producto") ProductoEntity producto,
            BindingResult result,
            Model model/*, SessionStatus status*/,
            @RequestPart(value = "file") FilePart file){
        // Se sebe registrar la entidad debido a que no se llama igual que el atributo usando @ModelAttribute("")
        if (result.hasErrors()){
            model.addAttribute("titulo", "Error en Validación de Producto");
            return Mono.just("formulario");
        }
        if (producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }
        //status.setComplete();
        Mono<CategoriaEntity> categoria = categoryHandler.execGetById(producto.getCategoria().getId());
        return categoria.flatMap(c -> { // Flujo Mono de Categoria se usa para asignar la NUEVA Categoria del Producto
            if(!file.filename().isEmpty()){
                producto.setFoto(UUID.randomUUID().toString().concat(file.filename())
                        .replace(" ", "")
                        .replace(":", "")
                        .replace("\\", "")
                        .replace("$", "")
                );
            }
            producto.setCategoria(c);
            return productHandler.execSaveProduct(producto); // FlaMap transforma a Flujo Mono de Producto
        })
                .doOnNext(p -> log.info("Producto <".concat(producto.getId()).concat(" ").concat(p.getNombre()).concat("> insertado.")))
                .flatMap(p -> {
                    if(!file.filename().isEmpty()){
                        return file.transferTo(new File(uploadPath.concat(producto.getFoto())));
                    }
                    return Mono.empty();
                })
                .thenReturn("redirect:/productos?error=producto+guardado+con+exito"); // Pero en realidad retorno Flujo Mono de String
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

    @GetMapping("/productos/v2/{id}")
    public Mono<String> actualizarV2(@PathVariable String id, Model model){
        return productHandler.execGetById(id)
                .doOnNext(p -> {
                    log.info("Producto <".concat(p.getId()).concat(" ").concat(p.getNombre()).concat("> preparado para modificar."));
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", "Actualizar Producto");
                })
                // Para que al fallar devuelva la instancia de Producto vacia y no error.
                .defaultIfEmpty(new ProductoEntity())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto."));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("formulario"))
                .onErrorResume(ex -> Mono.just("redirect:/productos?error=no+existe+el+producto"));
    }

    @GetMapping("/productos/el/{id}")
    public Mono<String> eliminar(@PathVariable String id){
        return productHandler.execGetById(id)
                .defaultIfEmpty(new ProductoEntity())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto a eliminar."));
                    }
                    return Mono.just(p);
                })
                .flatMap(productHandler::execDeleteProduct)
                .then(Mono.just("redirect:/productos?success=producto+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/productos?error=no+existe+el+producto+a+eliminar"));
    }

    @ModelAttribute("categorias")
    public Flux<CategoriaEntity> listarCategorias(){
        return categoryHandler.execGetCatagories();
    }

    @GetMapping("/productos/re/{id}")
    public Mono<String> detalle(Model model, @PathVariable String id){
        return productHandler.execGetById(id)
                .doOnNext(p -> {
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", "Detalle del Producto");
                })
                .switchIfEmpty(Mono.just(new ProductoEntity()))
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto a eliminar."));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("detalle"))
                .onErrorResume(ex -> Mono.just("redirect:/productos?error=no+existe+el+producto+a+detallar"));
    }

    @GetMapping("/uploads/{nombreFoto:.+}")
    public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String nombreFoto) throws MalformedURLException {
        Path ruta = Paths.get(uploadPath).resolve(nombreFoto).toAbsolutePath();
        Resource imagen = new UrlResource(ruta.toUri());
        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"".concat(imagen.getFilename()).concat("\""))
                        .body(imagen)
        );
    }
}
