package me.ingjedagobethm.springboot.webflux.app;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.CategoryHandler;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.Date;

@RequiredArgsConstructor
@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	private final ProductHandler productHandler;
	private final CategoryHandler categoryHandler;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) {

		categoryHandler.execDropCollection("categorias").subscribe();
		productHandler.execDropCollection("productos").subscribe();

		CategoriaEntity hogar = CategoriaEntity.builder().nombre("Hogar").build();
		CategoriaEntity deportes = CategoriaEntity.builder().nombre("Deportes").build();
		CategoriaEntity electronica = CategoriaEntity.builder().nombre("Electrónica").build();
		CategoriaEntity moda = CategoriaEntity.builder().nombre("Moda").build();

		Flux.just(hogar, deportes, electronica, moda)
				.flatMap(categoryHandler::execSaveCategory)
				// Se incluye otro flujo Flux. Para incluir un Mono se usa then.
				.thenMany(
						Flux.just(
									ProductoEntity.builder().nombre("TV Panasonic Led 40\"").precio(1440000.0).categoria(electronica).build(),
									ProductoEntity.builder().nombre("TV Sony Led 40\"").precio(2600000.0).categoria(electronica).build(),
									ProductoEntity.builder().nombre("TV LG Led 40\"").precio(1600000.0).categoria(electronica).build(),
									ProductoEntity.builder().nombre("Nevera Samsung NoFrost 180lts").precio(2200000.0).categoria(hogar).build(),
									ProductoEntity.builder().nombre("Nevera Centrales Frost 140lts").precio(1050000.0).categoria(hogar).build(),
									ProductoEntity.builder().nombre("Nevera Haceb Frost 160lts").precio(900000.0).categoria(hogar).build(),
									ProductoEntity.builder().nombre("Bicicleta Venzo VLZ").precio(3200000.0).categoria(deportes).build(),
									ProductoEntity.builder().nombre("Bicicleta Shimano Genérica").precio(905000.0).categoria(deportes).build(),
									ProductoEntity.builder().nombre("Suéter Hombre").precio(60000.0).categoria(moda).build(),
									ProductoEntity.builder().nombre("Suéter Mujer").precio(60000.0).categoria(moda).build()
								)
								.flatMap(producto -> {
									producto.setCreateAt(new Date());
									return productHandler.execSaveProduct(producto);
								})
				)
				.subscribe();

		/*
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
					return productHandler.execSaveProduct(producto);
				})
				.subscribe(producto -> log.info(producto.getNombre()));
		 */
	}

}
