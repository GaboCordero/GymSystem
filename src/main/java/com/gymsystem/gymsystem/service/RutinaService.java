package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Ejercicio;
import com.gymsystem.gymsystem.domain.Rutina;
import com.gymsystem.gymsystem.domain.RutinaEjercicio;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.EjercicioRepository;
import com.gymsystem.gymsystem.repository.RutinaEjercicioRepository;
import com.gymsystem.gymsystem.repository.RutinaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final SesionService sesionService;

    @Transactional(readOnly = true)
    public List<Rutina> getRutinas(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return rutinaRepository.findByEmpresa_IdEmpresaOrderByNombreAsc(
                entrenador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public List<Rutina> getRutinasActivas(String usernameActual) {
        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        return rutinaRepository.findByEmpresa_IdEmpresaAndActivoTrueOrderByNombreAsc(
                entrenador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public Rutina getRutina(Integer idRutina, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");

        Rutina rutina = rutinaRepository.findById(idRutina).orElse(null);

        if (rutina == null || !sesionService.mismaEmpresa(
                entrenador, rutina.getEmpresa().getIdEmpresa())) {
            return null;
        }

        return rutina;
    }

    @Transactional(readOnly = true)
    public List<RutinaEjercicio> getEjerciciosDeRutina(Integer idRutina) {
        return rutinaEjercicioRepository.findByRutina_IdRutinaOrderByOrdenAsc(idRutina);
    }

    @Transactional
    public Rutina guardar(Rutina datos, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");
        Integer idEmpresa = entrenador.getEmpresa().getIdEmpresa();

        validarDatos(datos);

        Rutina rutina;

        if (datos.getIdRutina() == null) {
            rutina = new Rutina();
            rutina.setEmpresa(entrenador.getEmpresa());
            rutina.setActivo(true);
        } else {
            rutina = rutinaRepository.findById(datos.getIdRutina())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

            if (!sesionService.mismaEmpresa(entrenador, rutina.getEmpresa().getIdEmpresa())) {
                throw new IllegalArgumentException("La rutina no pertenece a su gimnasio");
            }
        }

        String nombre = datos.getNombre().trim();

        Rutina repetida = rutinaRepository.findByEmpresa_IdEmpresaAndNombre(idEmpresa, nombre);

        if (repetida != null && !repetida.getIdRutina().equals(rutina.getIdRutina())) {
            throw new IllegalArgumentException("Ya existe una rutina con ese nombre");
        }

        rutina.setNombre(nombre);
        rutina.setObjetivo(datos.getObjetivo());
        rutina.setDescripcion(datos.getDescripcion());

        return rutinaRepository.save(rutina);
    }

    @Transactional
    public void cambiarEstado(Integer idRutina, String usernameActual) {

        Rutina rutina = getRutina(idRutina, usernameActual);

        if (rutina == null) {
            throw new IllegalArgumentException("Rutina no encontrada");
        }

        rutina.setActivo(!rutina.isActivo());
        rutinaRepository.save(rutina);
    }

    @Transactional
    public void agregarEjercicio(Integer idRutina, Integer idEjercicio,
            Integer series, String repeticiones, Integer descansoSegundos,
            String indicaciones, String usernameActual) {

        Rutina rutina = getRutina(idRutina, usernameActual);

        if (rutina == null) {
            throw new IllegalArgumentException("Rutina no encontrada");
        }

        if (idEjercicio == null || series == null
                || repeticiones == null || repeticiones.isBlank()) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        Ejercicio ejercicio = ejercicioRepository.findById(idEjercicio)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));

        if (!ejercicio.getEmpresa().getIdEmpresa().equals(rutina.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("El ejercicio no pertenece a su gimnasio");
        }

        if (!ejercicio.isActivo()) {
            throw new IllegalArgumentException("El ejercicio seleccionado está inactivo");
        }

        if (rutinaEjercicioRepository.existsByRutina_IdRutinaAndEjercicio_IdEjercicio(
                idRutina, idEjercicio)) {
            throw new IllegalArgumentException("El ejercicio ya forma parte de la rutina");
        }

        if (series < 1 || series > 20) {
            throw new IllegalArgumentException("Las series deben estar entre 1 y 20");
        }

        if (repeticiones.trim().length() > 20
                || (indicaciones != null && indicaciones.length() > 255)) {
            throw new IllegalArgumentException("Uno o más campos superan la longitud permitida");
        }

        if (descansoSegundos != null && (descansoSegundos < 0 || descansoSegundos > 600)) {
            throw new IllegalArgumentException("El descanso debe estar entre 0 y 600 segundos");
        }

        Integer ultimoOrden = rutinaEjercicioRepository.obtenerUltimoOrden(idRutina);

        RutinaEjercicio detalle = new RutinaEjercicio();
        detalle.setRutina(rutina);
        detalle.setEjercicio(ejercicio);
        detalle.setOrden(ultimoOrden == null ? 1 : ultimoOrden + 1);
        detalle.setSeries(series);
        detalle.setRepeticiones(repeticiones.trim());
        detalle.setDescansoSegundos(descansoSegundos);
        detalle.setIndicaciones(indicaciones);

        rutinaEjercicioRepository.save(detalle);
    }

    @Transactional
    public Integer quitarEjercicio(Integer idRutinaEjercicio, String usernameActual) {

        Usuario entrenador = sesionService.obtenerUsuario(usernameActual, "ENTRENADOR");

        RutinaEjercicio detalle = rutinaEjercicioRepository.findById(idRutinaEjercicio)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado en la rutina"));

        if (!sesionService.mismaEmpresa(entrenador,
                detalle.getRutina().getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("La rutina no pertenece a su gimnasio");
        }

        Integer idRutina = detalle.getRutina().getIdRutina();
        rutinaEjercicioRepository.delete(detalle);

        return idRutina;
    }

    private void validarDatos(Rutina rutina) {

        if (rutina.getNombre() == null || rutina.getNombre().isBlank()) {
            throw new IllegalArgumentException("Complete los campos obligatorios");
        }

        if (rutina.getNombre().trim().length() > 100
                || (rutina.getObjetivo() != null && rutina.getObjetivo().length() > 100)
                || (rutina.getDescripcion() != null && rutina.getDescripcion().length() > 255)) {
            throw new IllegalArgumentException("Uno o más campos superan la longitud permitida");
        }
    }
}