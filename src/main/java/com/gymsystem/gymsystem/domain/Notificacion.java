package com.gymsystem.gymsystem.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_notificacion;
    private Integer idUsuario;
    private String tipo;
    @Column(columnDefinition = "TEXT")
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida = false;
}