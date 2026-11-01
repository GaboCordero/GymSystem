package com.gymsystem.gymsystem.config;

import com.gymsystem.gymsystem.domain.Rol;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.RolRepository;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInicialConfig {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(name = "gymsystem.admin.inicializar", havingValue = "true")
    public CommandLineRunner crearSuperadmin() {
        return args -> {

            String username = System.getenv("GYMSYSTEM_ADMIN_USERNAME");
            String password = System.getenv("GYMSYSTEM_ADMIN_PASSWORD");

            if (username == null || username.isBlank()
                    || password == null || password.isBlank()) {
                throw new IllegalStateException("Faltan las credenciales del superadministrador inicial");
            }

            if (usuarioRepository.findByUsername(username) != null) {
                return;
            }

            Rol rol = rolRepository.findByRol("SUPERADMIN");

            if (rol == null) {
                throw new IllegalStateException("El rol SUPERADMIN no existe");
            }

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setNombre("Super");
            usuario.setApellidos("Administrador");
            usuario.setCorreo("admin.inicial@gymsystem.local");
            usuario.setActivo(true);
            usuario.setRoles(List.of(rol));

            usuarioRepository.save(usuario);
        };
    }
}

