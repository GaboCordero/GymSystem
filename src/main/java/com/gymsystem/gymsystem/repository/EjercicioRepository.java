package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Ejercicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {

    List<Ejercicio> findByEmpresa_IdEmpresaOrderByNombreAsc(Integer idEmpresa);

    List<Ejercicio> findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(Integer idEmpresa);

    Ejercicio findByEmpresa_IdEmpresaAndNombre(Integer idEmpresa, String nombre);
}