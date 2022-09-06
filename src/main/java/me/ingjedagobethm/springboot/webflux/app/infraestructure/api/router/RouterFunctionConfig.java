package me.ingjedagobethm.springboot.webflux.app.infraestructure.api.router;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.application.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@RequiredArgsConstructor
@Configuration
public class RouterFunctionConfig {

    private final ProductHandler productHandler;

    @Bean
    public RouterFunction<ServerResponse> routes(){
        return RouterFunctions
                // Se puede agregar otro path con .or(RequestPredicates.GET("/api/v3/productos"))
                .route(RequestPredicates.GET("/api/v2/productos"), productHandler::execGetProductsRouter)
                // Se puede validar el header con .and(contentType(MediaType.APPLICATION_JSON))
                .andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), productHandler::execGetProductByIdRouter)
                .andRoute(RequestPredicates.POST("/api/v2/productos"), productHandler::execSaveProductRouter)
                .andRoute(RequestPredicates.PUT("/api/v2/productos/{id}"), productHandler::execEditProductRouter)
                .andRoute(RequestPredicates.DELETE("/api/v2/productos/{id}"), productHandler::execDeleteProductRouter)
                .andRoute(RequestPredicates.POST("/api/v2/productos/upload/{id}"), productHandler::execUploadRouter)
                .andRoute(RequestPredicates.POST("/api/v2/productos/upload"), productHandler::execUploadProductRouter);

    }
}
