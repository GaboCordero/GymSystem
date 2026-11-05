package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Rutina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository extends JpaRepository<Rutina, Integer> {

    List<Rutina> findByEmpresa_IdEmpresaOrderByNombreAsc(Integer idEmpresa);

    List<Rutina> findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(Integer idEmpresa);

    Rutina findByEmpresa_IdEmpresaAndNombre(Integer idEmpresa, String nombre);
}