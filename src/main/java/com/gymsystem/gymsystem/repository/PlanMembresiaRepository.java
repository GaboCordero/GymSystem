package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.PlanMembresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanMembresiaRepository extends JpaRepository<PlanMembresia, Integer> {

    List<PlanMembresia> findByEmpresa_IdEmpresaOrderByNombreAsc(Integer idEmpresa);

    List<PlanMembresia> findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(Integer idEmpresa);

    PlanMembresia findByEmpresa_IdEmpresaAndNombre(Integer idEmpresa, String nombre);
}