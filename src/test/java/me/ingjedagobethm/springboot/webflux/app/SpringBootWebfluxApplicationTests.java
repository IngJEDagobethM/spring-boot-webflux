package me.ingjedagobethm.springboot.webflux.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ingjedagobethm.springboot.webflux.app.application.handler.CategoryHandler;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.ProductoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApplicationTests {

	@Autowired
	private WebTestClient client;
	@Autowired
	private ProductHandler productHandler;
	@Autowired
	private CategoryHandler categoryHandler;
	@Value(value = "${config.base.endpoint}")
	private String url;

	@Test
	void contextLoads() {
	}

	@Test
	void when_get_products_router(){
		client.get()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON)
				// Envia el request
				.exchange()
				// Assert Response HttpStatus.OK
				.expectStatus().isOk()
				// Assert Header Response contenga MediaType=JSON
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				// Assert Body Response contenga una estructura de tipo Entidad
				.expectBodyList(ProductoEntity.class)
				//.hasSize(10) // Número de registros
				.consumeWith(response -> {
					List<ProductoEntity> productos = response.getResponseBody();
					productos.forEach(p -> {
						System.out.println(p.getNombre());
					});
					Assertions.assertTrue(productos.size() > 0);
				})
		;
	}

	@Test
	void when_get_productById_router(){
		Mono<ProductoEntity> producto = productHandler.execFindByNombre("Bicicleta Venzo VLZ");
		client.get()
				// En pruebas unitarias el flujo debe ser síncrono, por lo tanto, se usa el .block()
				.uri(url.concat("/{id}"), Collections.singletonMap("id", producto.block().getId()))
				.accept(MediaType.APPLICATION_JSON)
				// Envia el request
				.exchange()
				// Assert Response HttpStatus.OK
				.expectStatus().isOk()
				// Assert Header Response contenga MediaType=JSON
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				// Si no se define el tipo esperado, se recibe el JSON
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("Bicicleta Venzo VLZ")
		;
	}

	@Test
	void when_save_product_router(){
		CategoriaEntity categoria = categoryHandler.execGetByNombre("Moda").block();
		client.post()
				.uri(url)
				// MediaType aceptado en el Request
				.contentType(MediaType.APPLICATION_JSON)
				// MediaType aceptado en el Response
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(ProductoEntity.builder().nombre("Suéter Niño").precio(45000.0).categoria(categoria).build())
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				//.expectBody(ProductoEntity.class) // Válido para el test de RouterFunctionConfig
				// Para el test de ApiRestController se debe implementar así, debido a que está respondiendo Map<k,v>
				.expectBody(new ParameterizedTypeReference<LinkedHashMap<String,Object>>() {})
				.consumeWith(response -> {
					Object o = response.getResponseBody().get("producto");
					ProductoEntity producto = new ObjectMapper().convertValue(o, ProductoEntity.class);
					Assertions.assertFalse(producto.getId().isEmpty());
					Assertions.assertTrue(producto.getNombre().equalsIgnoreCase("Suéter Niño"));
				});
	}

	@Test
	void when_update_product_router(){
		ProductoEntity producto = productHandler.execFindByNombre("Suéter Hombre").block();
		CategoriaEntity categoria = categoryHandler.execGetByNombre("Moda").block();
		ProductoEntity new_producto = ProductoEntity.builder().nombre("Suéter Unisex").precio(30000.0).categoria(categoria).build();
		client.put()
				.uri(url.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(new_producto)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ProductoEntity.class)
				.consumeWith(response -> {
					ProductoEntity p = response.getResponseBody();
					Assertions.assertFalse(p.getId().isEmpty());
					Assertions.assertTrue(p.getNombre().equalsIgnoreCase("Suéter Unisex"));
				});
	}

	@Test
	void when_delete_product_router(){
		ProductoEntity producto = productHandler.execFindByNombre("Suéter Mujer").block();
		client.delete()
				.uri(url.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty()
		;

		client.get()
				.uri(url.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody().isEmpty()
		;
	}

}
