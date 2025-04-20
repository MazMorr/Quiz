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

    @Column(nullable = false)
    private String resolucion;

    @Column(nullable = false)
    private int modoPantalla;

    @Column(nullable = false)
    private boolean esNuevo;

    @Column(nullable = false)
    private String rutaCarpetas;
}
