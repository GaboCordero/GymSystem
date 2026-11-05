package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.PlanMembresia;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.PlanMembresiaRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanMembresiaService {

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private final PlanMembresiaRepository planMembresiaRepository;
    private final SesionService sesionService;

    @Transactional(readOnly = true)
    public List<PlanMembresia> getPlanes(String usernameActual) {
        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");
        return planMembresiaRepository.findByEmpresa_IdEmpresaOrderByNombreAsc(
                administrador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public List<PlanMembresia> getPlanesActivos(String usernameActual) {
        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");
        return planMembresiaRepository.findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(
                administrador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public PlanMembresia getPlan(Integer idPlan, String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");

        PlanMembresia plan = planMembresiaRepository.findById(idPlan).orElse(null);

        if (plan == null || !sesionService.mismaEmpresa(
                administrador, plan.getEmpresa().getIdEmpresa())) {
            return null;
        }

        return plan;
    }

    @Transactional
    public void guardar(PlanMembresia datos, String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");
        Integer idEmpresa = administrador.getEmpresa().getIdEmpresa();

        validarDatos(datos);

        PlanMembresia plan;

        if (datos.getIdPlan() == null) {
            plan = new PlanMembresia();
            plan.setEmpresa(administrador.getEmpresa());
            plan.setActivo(true);
        } else {
            plan = planMembresiaRepository.findById(datos.getIdPlan())
                    .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

            if (!sesionService.mismaEmpresa(administrador, plan.getEmpresa().getIdEmpresa())) {
                throw new IllegalArgumentException("El plan no pertenece a su gimnasio");
            }
        }

        String nombre = datos.getNombre().trim();

        PlanMembresia repetido = planMembresiaRepository
                .findByEmpresa_IdEmpresaAndNombre(idEmpresa, nombre);

        if (repetido != null && !repetido.getIdPlan().equals(plan.getIdPlan())) {
            throw new IllegalArgumentException("Ya existe un plan con ese nombre");
        }

        plan.setNombre(nombre);
        plan.setDescripcion(datos.getDescripcion());
        plan.setDuracionDias(datos.getDuracionDias());
        plan.setPrecio(datos.getPrecio());

        planMembresiaRepository.save(plan);
    }

    @Transactional
    public void cambiarEstado(Integer idPlan, String usernameActual) {

        PlanMembresia plan = getPlan(idPlan, usernameActual);

        if (plan == null) {
            throw new IllegalArgumentException("Plan no encontrado");
        }

        plan.setActivo(!plan.isActivo());
        planMembresiaRepository.save(plan);
    }

    private void validarDatos(PlanMembresia plan) {

        if (plan.getNombre() == null || plan.getNombre().isBlank()
                || plan.getDuracionDias() == null
                || plan.getPrecio() == null) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        if (plan.getNombre().trim().length() > 50
                || (plan.getDescripcion() != null
                && plan.getDescripcion().length() > 255)) {
            throw new IllegalArgumentException("Uno o más campos superan la longitud permitida");
        }

        if (plan.getDuracionDias() < 1 || plan.getDuracionDias() > 3650) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 3650 días");
        }

        if (plan.getPrecio().compareTo(BigDecimal.ZERO) <= 0
                || plan.getPrecio().compareTo(PRECIO_MAXIMO) > 0
                || plan.getPrecio().scale() > 2) {
            throw new IllegalArgumentException("El precio no es válido");
        }
    }
}