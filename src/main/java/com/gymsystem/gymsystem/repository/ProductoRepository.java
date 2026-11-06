package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByIdEmpresa(Integer idEmpresa);
}
