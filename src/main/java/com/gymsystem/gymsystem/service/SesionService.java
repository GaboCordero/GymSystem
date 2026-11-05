package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(String username, String rol) {

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null
                || !usuario.isActivo()
                || usuario.getEmpresa() == null
                || !usuario.getEmpresa().isActivo()
                || usuario.getRoles().stream()
                        .noneMatch(r -> r.getRol().equals(rol))) {
            throw new AccessDeniedException("Acceso no autorizado");
        }

        return usuario;
    }

    public boolean mismaEmpresa(Usuario usuario, Integer idEmpresa) {
        return idEmpresa != null
                && usuario.getEmpresa().getIdEmpresa().equals(idEmpresa);
    }
}