package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Membresia;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    List<Membresia> findByCliente_Usuario_Empresa_IdEmpresaOrderByFechaInicioDesc(Integer idEmpresa);

    List<Membresia> findByCliente_IdClienteAndEstadoIn(Integer idCliente, List<String> estados);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Membresia m SET m.estado = 'VENCIDA' "
            + "WHERE m.estado IN ('PENDIENTE', 'ACTIVA') AND m.fechaFin < :hoy")
    int marcarVencidas(@Param("hoy") LocalDate hoy);
}