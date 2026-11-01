package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Cliente;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.ClienteRepository;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getClientesPorEmpresa(Integer idEmpresa) {
        return clienteRepository.findByUsuario_Empresa_IdEmpresa(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Cliente getCliente(Integer idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    @Transactional
    public void registrarCliente(Integer idUsuario, String observaciones,
            String usernameActual) {

        Usuario administrador = usuarioRepository.findByUsername(usernameActual);

        if (administrador == null || !administrador.isActivo()
                || administrador.getEmpresa() == null
                || !administrador.getEmpresa().isActivo()
                || administrador.getRoles().stream()
                        .noneMatch(r -> r.getRol().equals("ADMIN"))) {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario está inactivo");
        }

        if (usuario.getEmpresa() == null
                || !administrador.getEmpresa().getIdEmpresa()
                        .equals(usuario.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("El usuario no pertenece a su gimnasio");
        }

        boolean esCliente = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("CLIENTE"));

        if (!esCliente) {
            throw new IllegalArgumentException("El usuario no tiene el rol CLIENTE");
        }

        if (clienteRepository.existsByUsuario_IdUsuario(idUsuario)) {
            throw new IllegalArgumentException("Este usuario ya está registrado como cliente");
        }

        if (observaciones != null && observaciones.length() > 255) {
            throw new IllegalArgumentException("Las observaciones superan la longitud permitida");
        }

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setObservaciones(observaciones);

        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public boolean existeCliente(Integer idUsuario) {
        return clienteRepository.existsByUsuario_IdUsuario(idUsuario);
    }
}
