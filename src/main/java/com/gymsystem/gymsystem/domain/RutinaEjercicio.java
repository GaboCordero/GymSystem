package com.gymsystem.gymsystem.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "rutina_ejercicio")
public class RutinaEjercicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_ejercicio")
    private Integer idRutinaEjercicio;

    @ManyToOne
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Integer series;

    @Column(length = 20, nullable = false)
    private String repeticiones;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(length = 255)
    private String indicaciones;
}