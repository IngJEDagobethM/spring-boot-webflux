package me.ingjedagobethm.springboot.webflux.app.infraestructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
public class ProductoEntity {
    @Id
    private String id;
    @NotEmpty
    private String nombre;
    @NotNull
    private Double precio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    private CategoriaEntity categoria;
}
