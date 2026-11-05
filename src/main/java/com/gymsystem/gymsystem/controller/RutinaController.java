package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Rutina;
import com.gymsystem.gymsystem.service.EjercicioService;
import com.gymsystem.gymsystem.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rutina")
public class RutinaController {

    private final RutinaService rutinaService;
    private final EjercicioService ejercicioService;

    @InitBinder("rutina")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idRutina", "nombre", "objetivo", "descripcion");
    }

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("rutinas", rutinaService.getRutinas(authentication.getName()));
        return "rutina/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("rutina")) {
            model.addAttribute("rutina", new Rutina());
        }
        return "rutina/modifica";
    }

    @GetMapping("/modificar/{idRutina}")
    public String modificar(@PathVariable Integer idRutina, Model model,
            Authentication authentication) {

        if (!model.containsAttribute("rutina")) {
            Rutina rutina = rutinaService.getRutina(idRutina, authentication.getName());

            if (rutina == null) {
                return "redirect:/rutina/listado";
            }

            model.addAttribute("rutina", rutina);
        }

        return "rutina/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("rutina") Rutina rutina,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Rutina guardada = rutinaService.guardar(rutina, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje",
                    "Rutina guardada correctamente. Agregue o revise sus ejercicios.");

            return "redirect:/rutina/detalle/" + guardada.getIdRutina();

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("rutina", rutina);

            return rutina.getIdRutina() == null
                    ? "redirect:/rutina/nuevo"
                    : "redirect:/rutina/modificar/" + rutina.getIdRutina();
        }
    }

    @PostMapping("/estado/{idRutina}")
    public String cambiarEstado(@PathVariable Integer idRutina,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            rutinaService.cambiarEstado(idRutina, authentication.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Estado de la rutina actualizado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/rutina/listado";
    }

    @GetMapping("/detalle/{idRutina}")
    public String detalle(@PathVariable Integer idRutina, Model model,
            Authentication authentication) {

        Rutina rutina = rutinaService.getRutina(idRutina, authentication.getName());

        if (rutina == null) {
            return "redirect:/rutina/listado";
        }

        model.addAttribute("rutina", rutina);
        model.addAttribute("detalles", rutinaService.getEjerciciosDeRutina(idRutina));
        model.addAttribute("ejercicios", ejercicioService.getEjerciciosActivos(authentication.getName()));

        return "rutina/detalle";
    }

    @PostMapping("/ejercicio/agregar")
    public String agregarEjercicio(@RequestParam Integer idRutina,
            @RequestParam(required = false) Integer idEjercicio,
            @RequestParam(required = false) Integer series,
            @RequestParam(required = false) String repeticiones,
            @RequestParam(required = false) Integer descansoSegundos,
            @RequestParam(required = false) String indicaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            rutinaService.agregarEjercicio(idRutina, idEjercicio, series,
                    repeticiones, descansoSegundos, indicaciones, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Ejercicio agregado a la rutina");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/rutina/detalle/" + idRutina;
    }

    @PostMapping("/ejercicio/quitar/{idRutinaEjercicio}")
    public String quitarEjercicio(@PathVariable Integer idRutinaEjercicio,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Integer idRutina = rutinaService.quitarEjercicio(
                    idRutinaEjercicio, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Ejercicio quitado de la rutina");

            return "redirect:/rutina/detalle/" + idRutina;

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/rutina/listado";
        }
    }
}