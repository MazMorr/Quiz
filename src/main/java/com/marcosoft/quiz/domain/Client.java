package com.marcosoft.quiz.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.IdGeneratorType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ConfiguracionCliente")
public class Client {

    @Id
    private int id;

    @Column(nullable = false, name= "resolucion")
    private String resolution;

    @Column(nullable = false, name= "modo_Pantalla")
    private int windowMode;

    @Column(nullable = false, name = "es_nuevo")
    private boolean isNew;

    @Column(nullable = false, name="ruta_archivos")
    private String folderPath;

    @Column(nullable = false)
    private int questionNumber;

    @Column(nullable = false)
    private int thematicNumber;
}
