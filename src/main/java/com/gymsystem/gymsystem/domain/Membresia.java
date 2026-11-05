package com.gymsystem.gymsystem.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "membresia")
public class Membresia implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ACTIVA = "ACTIVA";
    public static final String VENCIDA = "VENCIDA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer idMembresia;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_plan", nullable = false)
    private PlanMembresia plan;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;
}