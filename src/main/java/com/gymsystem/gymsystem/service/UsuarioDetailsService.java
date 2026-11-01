package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        String[] roles = usuario.getRoles().stream()
                .map(rol -> rol.getRol())
                .toArray(String[]::new);

        boolean activo = usuario.isActivo()
                && (usuario.getEmpresa() == null || usuario.getEmpresa().isActivo());

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(roles)
                .disabled(!activo)
                .build();
    }
}
