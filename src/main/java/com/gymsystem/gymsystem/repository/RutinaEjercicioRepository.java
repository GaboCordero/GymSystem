package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.RutinaEjercicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Integer> {

    List<RutinaEjercicio> findByRutina_IdRutinaOrderByOrdenAsc(Integer idRutina);

    boolean existsByRutina_IdRutinaAndEjercicio_IdEjercicio(Integer idRutina, Integer idEjercicio);

    long countByRutina_IdRutina(Integer idRutina);

    @Query("SELECT MAX(re.orden) FROM RutinaEjercicio re "
            + "WHERE re.rutina.idRutina = :idRutina")
    Integer obtenerUltimoOrden(@Param("idRutina") Integer idRutina);
}