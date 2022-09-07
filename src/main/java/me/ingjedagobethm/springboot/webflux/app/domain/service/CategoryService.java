package me.ingjedagobethm.springboot.webflux.app.domain.service;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.app.domain.repository.CategoryRepository;
import me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity.CategoriaEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Mono<CategoriaEntity> saveCategory(CategoriaEntity categoriaEntity){
        return categoryRepository.saveCategory(categoriaEntity);
    }

    public Flux<CategoriaEntity> getCategories(){
        return categoryRepository.getCategories();
    }

    public Mono<CategoriaEntity> getCategoryById(String id){
        return categoryRepository.getCategoryById(id);
    }

    public Mono<Void> dropCollection(String colection){
        return  categoryRepository.dropColection(colection);
    }

    public Mono<CategoriaEntity> getCategoryByNombre(String nombre){
        return categoryRepository.getCategoryByNombre(nombre);
    }
}
