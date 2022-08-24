package me.ingjedagobethm.springboot.webflux.app;

import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	@Autowired
	private final ProductoRepository productoRepository;

	public SpringBootWebfluxApplication(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) {

		productoRepository.eliminarColeccion("productos").subscribe();

		// map no sirve porque devuelve un Mono<ProductoEntity>, en cambio,
		// flatmap lo integra al mismo flujo retornando ProductoEntity
		Flux.just(
                ProductoEntity.builder().nombre("TV Panasonic Led 40\"").precio(1440000.0).build(),
                ProductoEntity.builder().nombre("TV Sony Led 40\"").precio(2600000.0).build(),
				ProductoEntity.builder().nombre("TV LG Led 40\"").precio(1600000.0).build(),
				ProductoEntity.builder().nombre("Nevera Samsung NoFrost 180lts").precio(2200000.0).build(),
				ProductoEntity.builder().nombre("Nevera Centrales Frost 140lts").precio(1050000.0).build(),
				ProductoEntity.builder().nombre("Nevera Haceb Frost 160lts").precio(900000.0).build()
        )
				.flatMap(producto -> {
					producto.setCreateAt(new Date());
					return productoRepository.insertarProducto(producto);
				})
				.subscribe(producto -> log.info(producto.getNombre()));
	}

}
