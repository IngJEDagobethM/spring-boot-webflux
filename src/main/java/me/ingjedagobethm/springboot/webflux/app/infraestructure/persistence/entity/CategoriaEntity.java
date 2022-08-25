package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categorias")
public class CategoriaEntity {

    @Id
    private String id;
    private String nombre;
}
