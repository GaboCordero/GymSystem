package com.gymsystem.gymsystem.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_venta;
    private Integer id_empresa;
    private Integer id_usuario;
    private Integer id_cliente;
    private LocalDateTime fecha;
    private Double total;
}