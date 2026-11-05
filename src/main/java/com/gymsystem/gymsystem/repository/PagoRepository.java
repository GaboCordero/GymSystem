package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Pago;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByMembresia_IdMembresiaOrderByFechaPagoDesc(Integer idMembresia);

    List<Pago> findByMembresia_Cliente_Usuario_Empresa_IdEmpresaOrderByFechaPagoDesc(Integer idEmpresa);

    @Query("SELECT SUM(p.monto) FROM Pago p "
            + "WHERE p.membresia.idMembresia = :idMembresia")
    BigDecimal sumarPagos(@Param("idMembresia") Integer idMembresia);
}