package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Ejercicio;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.EjercicioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final SesionService sesionService;

    @Transactional(readOnly = true)
    public List<Ejercicio> getEjercicios(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return ejercicioRepository.findByEmpresa_IdEmpresaOrderByNombreAsc(
                entrenador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public List<Ejercicio> getEjerciciosActivos(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return ejercicioRepository.findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(
                entrenador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public Ejercicio getEjercicio(Integer idEjercicio, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");

        Ejercicio ejercicio = ejercicioRepository.findById(idEjercicio).orElse(null);

        if (ejercicio == null || !sesionService.mismaEmpresa(
                entrenador, ejercicio.getEmpresa().getIdEmpresa())) {
            return null;
        }

        return ejercicio;
    }

    @Transactional
    public void guardar(Ejercicio datos, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        Integer idEmpresa = entrenador.getEmpresa().getIdEmpresa();

        validarDatos(datos);

        Ejercicio ejercicio;

        if (datos.getIdEjercicio() == null) {
            ejercicio = new Ejercicio();
            ejercicio.setEmpresa(entrenador.getEmpresa());
            ejercicio.setActivo(true);
        } else {
            ejercicio = ejercicioRepository.findById(datos.getIdEjercicio())
                    .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));

            if (!sesionService.mismaEmpresa(entrenador, ejercicio.getEmpresa().getIdEmpresa())) {
                throw new IllegalArgumentException("El ejercicio no pertenece a su gimnasio");
            }
        }

        String nombre = datos.getNombre().trim();

        Ejercicio repetido = ejercicioRepository.findByEmpresa_IdEmpresaAndNombre(idEmpresa, nombre);

        if (repetido != null && !repetido.getIdEjercicio().equals(ejercicio.getIdEjercicio())) {
            throw new IllegalArgumentException("Ya existe un ejercicio con ese nombre");
        }

        ejercicio.setNombre(nombre);
        ejercicio.setGrupoMuscular(datos.getGrupoMuscular().trim());
        ejercicio.setDescripcion(datos.getDescripcion());

        ejercicioRepository.save(ejercicio);
    }

    @Transactional
    public void cambiarEstado(Integer idEjercicio, String usernameActual) {

        Ejercicio ejercicio = getEjercicio(idEjercicio, usernameActual);

        if (ejercicio == null) {
            throw new IllegalArgumentException("Ejercicio no encontrado");
        }

        ejercicio.setActivo(!ejercicio.isActivo());
        ejercicioRepository.save(ejercicio);
    }

    private void validarDatos(Ejercicio ejercicio) {

        if (ejercicio.getNombre() == null || ejercicio.getNombre().isBlank()
                || ejercicio.getGrupoMuscular() == null
                || ejercicio.getGrupoMuscular().isBlank()) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        if (ejercicio.getNombre().trim().length() > 100
                || ejercicio.getGrupoMuscular().trim().length() > 50
                || (ejercicio.getDescripcion() != null
                && ejercicio.getDescripcion().length() > 255)) {
            throw new IllegalArgumentException("Uno o más campos superan la longitud permitida");
        }
    }
}