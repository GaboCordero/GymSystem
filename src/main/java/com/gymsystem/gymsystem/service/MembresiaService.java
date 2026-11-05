package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Cliente;
import com.gymsystem.gymsystem.domain.Membresia;
import com.gymsystem.gymsystem.domain.PlanMembresia;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.ClienteRepository;
import com.gymsystem.gymsystem.repository.MembresiaRepository;
import com.gymsystem.gymsystem.repository.PlanMembresiaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final PlanMembresiaRepository planMembresiaRepository;
    private final ClienteRepository clienteRepository;
    private final SesionService sesionService;

    @Transactional
    public List<Membresia> getMembresias(String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");

        membresiaRepository.marcarVencidas(LocalDate.now());

        return membresiaRepository.findByCliente_Usuario_Empresa_IdEmpresaOrderByFechaInicioDesc(
                administrador.getEmpresa().getIdEmpresa());
    }

    @Transactional
    public Membresia getMembresia(Integer idMembresia, String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");

        membresiaRepository.marcarVencidas(LocalDate.now());

        Membresia membresia = membresiaRepository.findById(idMembresia).orElse(null);

        if (membresia == null || !sesionService.mismaEmpresa(administrador,
                membresia.getCliente().getUsuario().getEmpresa().getIdEmpresa())) {
            return null;
        }

        return membresia;
    }

    @Transactional
    public Membresia asignarMembresia(Integer idCliente, Integer idPlan,
            LocalDate fechaInicio, String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");

        if (idCliente == null || idPlan == null || fechaInicio == null) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        membresiaRepository.marcarVencidas(LocalDate.now());

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Usuario usuarioCliente = cliente.getUsuario();

        if (usuarioCliente.getEmpresa() == null || !sesionService.mismaEmpresa(
                administrador, usuarioCliente.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("El cliente no pertenece a su gimnasio");
        }

        if (!usuarioCliente.isActivo()) {
            throw new IllegalArgumentException("La cuenta del cliente está inactiva");
        }

        PlanMembresia plan = planMembresiaRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

        if (!sesionService.mismaEmpresa(administrador, plan.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("El plan no pertenece a su gimnasio");
        }

        if (!plan.isActivo()) {
            throw new IllegalArgumentException("El plan seleccionado está inactivo");
        }

        LocalDate fechaFin = fechaInicio.plusDays(plan.getDuracionDias() - 1);

        if (fechaFin.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Con esa fecha de inicio la membresía ya estaría vencida");
        }

        if (fechaInicio.isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor a un año");
        }

        List<Membresia> vigentes = membresiaRepository.findByCliente_IdClienteAndEstadoIn(
                idCliente, List.of(Membresia.PENDIENTE, Membresia.ACTIVA));

        boolean traslape = vigentes.stream()
                .anyMatch(m -> !fechaInicio.isAfter(m.getFechaFin())
                        && !fechaFin.isBefore(m.getFechaInicio()));

        if (traslape) {
            throw new IllegalArgumentException(
                    "El cliente ya tiene una membresía vigente o pendiente en esas fechas");
        }

        Membresia membresia = new Membresia();
        membresia.setCliente(cliente);
        membresia.setPlan(plan);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaFin(fechaFin);
        membresia.setPrecio(plan.getPrecio());
        membresia.setEstado(Membresia.PENDIENTE);
        membresia.setFechaRegistro(LocalDate.now());

        return membresiaRepository.save(membresia);
    }
}