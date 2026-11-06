package com.gymsystem.gymsystem.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_venta")
@Data
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_detalle_venta;
    private Integer id_venta;
    private Integer id_producto;
    private Integer cantidad;
    private Double precio_unitario;
    private Double subtotal;
}