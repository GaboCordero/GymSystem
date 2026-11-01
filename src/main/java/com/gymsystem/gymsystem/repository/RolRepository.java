package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findByRol(String rol);
}
