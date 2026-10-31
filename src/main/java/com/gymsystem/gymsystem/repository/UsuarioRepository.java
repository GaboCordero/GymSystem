package com.gymsystem.gymsystem.repository;

import com.gymsystem.gymsystem.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsername(String username);

    Usuario findByCorreo(String correo);

}
