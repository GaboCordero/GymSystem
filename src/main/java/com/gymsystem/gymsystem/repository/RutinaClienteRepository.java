package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.RutinaCliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaClienteRepository extends JpaRepository<RutinaCliente, Integer> {

    List<RutinaCliente> findByRutina_Empresa_IdEmpresaOrderByIdRutinaClienteDesc(Integer idEmpresa);

    List<RutinaCliente> findByCliente_IdClienteAndActivaTrue(Integer idCliente);

    RutinaCliente findFirstByCliente_Usuario_IdUsuarioAndActivaTrueOrderByIdRutinaClienteDesc(Integer idUsuario);
}