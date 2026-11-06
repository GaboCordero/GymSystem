package com.gymsystem.gymsystem.domain;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_producto;

    @Column(name = "id_empresa", nullable = false)
    private Integer idEmpresa;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer existencias = 0;

    @Column(nullable = false)
    private Boolean activo = true;
}
