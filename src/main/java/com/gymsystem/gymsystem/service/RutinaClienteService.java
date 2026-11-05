package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Cliente;
import com.gymsystem.gymsystem.domain.Rutina;
import com.gymsystem.gymsystem.domain.RutinaCliente;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.ClienteRepository;
import com.gymsystem.gymsystem.repository.RutinaClienteRepository;
import com.gymsystem.gymsystem.repository.RutinaEjercicioRepository;
import com.gymsystem.gymsystem.repository.RutinaRepository;
import com.gymsystem.gymsystem.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RutinaClienteService {

    private final RutinaClienteRepository rutinaClienteRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionService sesionService;

    @Transactional(readOnly = true)
    public List<RutinaCliente> getAsignaciones(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return rutinaClienteRepository.findByRutina_Empresa_IdEmpresaOrderByIdRutinaClienteDesc(
                entrenador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public List<Cliente> getClientesDisponibles(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return clienteRepository.findByUsuario_Empresa_IdEmpresa(
                entrenador.getEmpresa().getIdEmpresa())
                .stream()
                .filter(c -> c.getUsuario().isActivo())
                .toList();
    }

    @Transactional
    public void asignarRutina(Integer idCliente, Integer idRutina,
            String observaciones, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");

        if (idCliente == null || idRutina == null) {
            throw new IllegalArgumentException("Seleccione el cliente y la rutina");
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        if (cliente.getUsuario().getEmpresa() == null || !sesionService.mismaEmpresa(
                entrenador, cliente.getUsuario().getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("El cliente no pertenece a su gimnasio");
        }

        if (!cliente.getUsuario().isActivo()) {
            throw new IllegalArgumentException("La cuenta del cliente está inactiva");
        }

        Rutina rutina = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        if (!sesionService.mismaEmpresa(entrenador, rutina.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("La rutina no pertenece a su gimnasio");
        }

        if (!rutina.isActivo()) {
            throw new IllegalArgumentException("La rutina seleccionada está inactiva");
        }

        if (rutinaEjercicioRepository.countByRutina_IdRutina(idRutina) == 0) {
            throw new IllegalArgumentException("La rutina no tiene ejercicios; agregue al menos uno");
        }

        if (observaciones != null && observaciones.length() > 255) {
            throw new IllegalArgumentException("Las observaciones superan la longitud permitida");
        }

        List<RutinaCliente> anteriores = rutinaClienteRepository
                .findByCliente_IdClienteAndActivaTrue(idCliente);

        for (RutinaCliente anterior : anteriores) {
            anterior.setActiva(false);
        }

        rutinaClienteRepository.saveAll(anteriores);

        RutinaCliente asignacion = new RutinaCliente();
        asignacion.setRutina(rutina);
        asignacion.setCliente(cliente);
        asignacion.setEntrenador(entrenador);
        asignacion.setFechaAsignacion(LocalDate.now());
        asignacion.setObservaciones(observaciones);
        asignacion.setActiva(true);

        rutinaClienteRepository.save(asignacion);
    }

    @Transactional(readOnly = true)
    public RutinaCliente getRutinaActual(String usernameCliente) {

        Usuario usuario = usuarioRepository.findByUsername(usernameCliente);

        if (usuario == null) {
            return null;
        }

        return rutinaClienteRepository
                .findFirstByCliente_Usuario_IdUsuarioAndActivaTrueOrderByIdRutinaClienteDesc(
                        usuario.getIdUsuario());
    }
}