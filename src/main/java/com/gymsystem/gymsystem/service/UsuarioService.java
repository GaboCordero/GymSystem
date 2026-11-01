package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymsystem.gymsystem.domain.Empresa;
import com.gymsystem.gymsystem.domain.Rol;
import com.gymsystem.gymsystem.repository.EmpresaRepository;
import com.gymsystem.gymsystem.repository.RolRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorEmpresa(Integer idEmpresa) {
        return usuarioRepository.findByEmpresa_IdEmpresa(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuario(Usuario usuario) {
        return usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return usuarioRepository.findByUsername(username) != null;
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo) != null;
    }

    @Transactional
    public void save(Usuario usuario) {

        if (usuario.getIdUsuario() == null) {

            if (existeUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya está registrado");
            }

            if (existeCorreo(usuario.getCorreo())) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado");
            }

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        } else {

            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!existente.getUsername().equals(usuario.getUsername())
                    && existeUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya está registrado");
            }

            if (!existente.getCorreo().equals(usuario.getCorreo())
                    && existeCorreo(usuario.getCorreo())) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado");
            }

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarUsuario(Usuario usuario, String rol,
            Integer idEmpresa, String usernameActual, boolean superadmin) {

        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual);

        if (usuarioActual == null || !usuarioActual.isActivo()) {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        if (superadmin) {
            if (!usuarioActual.getRoles().stream()
                    .anyMatch(r -> r.getRol().equals("SUPERADMIN"))
                    || !rol.equals("ADMIN")) {
                throw new IllegalArgumentException("Rol no permitido");
            }
        } else {
            if (!usuarioActual.getRoles().stream()
                    .anyMatch(r -> r.getRol().equals("ADMIN"))
                    || (!rol.equals("ENTRENADOR") && !rol.equals("CLIENTE"))) {
                throw new IllegalArgumentException("Rol no permitido");
            }

            if (usuarioActual.getEmpresa() == null
                    || !usuarioActual.getEmpresa().isActivo()) {
                throw new IllegalArgumentException("Empresa no válida");
            }

            idEmpresa = usuarioActual.getEmpresa().getIdEmpresa();
        }

        if (idEmpresa == null
                || !empresaRepository.existsByIdEmpresaAndActivoTrue(idEmpresa)) {
            throw new IllegalArgumentException("Empresa no válida");
        }

        validarDatos(usuario);
        
        if (existeUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }

        if (existeCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        Rol rolAsignado = rolRepository.findByRol(rol);

        if (rolAsignado == null) {
            throw new IllegalArgumentException("Rol no encontrado");
        }

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(usuario.getUsername());
        nuevo.setNombre(usuario.getNombre());
        nuevo.setApellidos(usuario.getApellidos());
        nuevo.setCorreo(usuario.getCorreo());
        nuevo.setTelefono(usuario.getTelefono());
        nuevo.setPassword(passwordEncoder.encode(usuario.getPassword()));
        nuevo.setEmpresa(empresa);
        nuevo.setRoles(List.of(rolAsignado));
        nuevo.setActivo(true);
        usuarioRepository.save(nuevo);
    }

    @Transactional
    public void modificarUsuario(Integer idUsuario, Usuario datos,
            String usernameActual) {

        Usuario administrador = usuarioRepository.findByUsername(usernameActual);

        if (administrador == null || !administrador.isActivo()) {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean superadmin = administrador.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("SUPERADMIN"));

        boolean admin = administrador.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("ADMIN"));

        boolean usuarioAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("ADMIN"));

        if (superadmin) {
            if (!usuarioAdmin || usuario.getEmpresa() == null) {
                throw new IllegalArgumentException("No puede modificar este usuario");
            }
        } else if (admin) {
            if (administrador.getEmpresa() == null
                    || !administrador.getEmpresa().isActivo()
                    || usuario.getEmpresa() == null
                    || !administrador.getEmpresa().getIdEmpresa()
                            .equals(usuario.getEmpresa().getIdEmpresa())
                    || usuarioAdmin
                    || usuario.getRoles().stream()
                            .anyMatch(rol -> rol.getRol().equals("SUPERADMIN"))) {
                throw new IllegalArgumentException("No puede modificar este usuario");
            }
        } else {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        validarDatos(datos);

        Usuario repetido = usuarioRepository.findByUsername(datos.getUsername());

        if (repetido != null && !repetido.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }

        repetido = usuarioRepository.findByCorreo(datos.getCorreo());

        if (repetido != null && !repetido.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        usuario.setNombre(datos.getNombre());
        usuario.setApellidos(datos.getApellidos());
        usuario.setUsername(datos.getUsername());
        usuario.setCorreo(datos.getCorreo());
        usuario.setTelefono(datos.getTelefono());

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarEstado(Integer idUsuario, String usernameActual) {

        Usuario administrador = usuarioRepository.findByUsername(usernameActual);

        if (administrador == null || !administrador.isActivo()) {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean superadmin = administrador.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("SUPERADMIN"));

        boolean admin = administrador.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("ADMIN"));

        boolean usuarioAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("ADMIN"));

        boolean usuarioSuperadmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("SUPERADMIN"));

        if (administrador.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("No puede desactivar su propia cuenta");
        }

        if (superadmin) {
            if (!usuarioAdmin || usuarioSuperadmin || usuario.getEmpresa() == null) {
                throw new IllegalArgumentException("No puede cambiar el estado de este usuario");
            }
        } else if (admin) {
            if (administrador.getEmpresa() == null
                    || !administrador.getEmpresa().isActivo()
                    || usuario.getEmpresa() == null
                    || !administrador.getEmpresa().getIdEmpresa()
                            .equals(usuario.getEmpresa().getIdEmpresa())
                    || usuarioAdmin
                    || usuarioSuperadmin) {
                throw new IllegalArgumentException("No puede cambiar el estado de este usuario");
            }
        } else {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    private void validarDatos(Usuario usuario) {

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getApellidos() == null || usuario.getApellidos().isBlank()
                || usuario.getUsername() == null || usuario.getUsername().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        if (usuario.getNombre().length() > 50
                || usuario.getApellidos().length() > 75
                || usuario.getUsername().length() > 50
                || usuario.getCorreo().length() > 100
                || (usuario.getTelefono() != null
                && usuario.getTelefono().length() > 25)) {
            throw new IllegalArgumentException("Uno o más campos superan la longitud permitida");
        }

        if (!usuario.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }
    }

}
