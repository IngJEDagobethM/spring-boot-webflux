package me.ingjedagobethm.springboot.webflux.app.application.handler;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.service.CategoryService;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CategoryHandler {

    private final CategoryService categoryService;

    public Mono<CategoriaEntity> execSaveCategory(CategoriaEntity categoriaEntity){
        return categoryService.saveCategory(categoriaEntity);
    }

    public Flux<CategoriaEntity> execGetCatagories(){
        return categoryService.getCategories();
    }

    public Mono<CategoriaEntity> execGetById(String id){
        return categoryService.getCategoryById(id);
    }

    public Mono<Void> execDropCollection(String colection){
        return  categoryService.dropCollection(colection);
    }
}
