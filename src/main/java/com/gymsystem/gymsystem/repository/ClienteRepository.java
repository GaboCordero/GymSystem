package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByUsuario_IdUsuario(Integer idUsuario);

    List<Cliente> findByUsuario_Empresa_IdEmpresa(Integer idEmpresa);
}
