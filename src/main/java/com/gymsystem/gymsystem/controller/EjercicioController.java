package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Ejercicio;
import com.gymsystem.gymsystem.service.EjercicioService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ejercicio")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @InitBinder("ejercicio")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idEjercicio", "nombre", "grupoMuscular", "descripcion");
    }

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("ejercicios", ejercicioService.getEjercicios(authentication.getName()));
        return "ejercicio/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("ejercicio")) {
            model.addAttribute("ejercicio", new Ejercicio());
        }
        return "ejercicio/modifica";
    }

    @GetMapping("/modificar/{idEjercicio}")
    public String modificar(@PathVariable Integer idEjercicio, Model model,
            Authentication authentication) {

        if (!model.containsAttribute("ejercicio")) {
            Ejercicio ejercicio = ejercicioService.getEjercicio(idEjercicio, authentication.getName());

            if (ejercicio == null) {
                return "redirect:/ejercicio/listado";
            }

            model.addAttribute("ejercicio", ejercicio);
        }

        return "ejercicio/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("ejercicio") Ejercicio ejercicio,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            ejercicioService.guardar(ejercicio, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Ejercicio guardado correctamente");

            return "redirect:/ejercicio/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("ejercicio", ejercicio);

            return ejercicio.getIdEjercicio() == null
                    ? "redirect:/ejercicio/nuevo"
                    : "redirect:/ejercicio/modificar/" + ejercicio.getIdEjercicio();
        }
    }

    @PostMapping("/estado/{idEjercicio}")
    public String cambiarEstado(@PathVariable Integer idEjercicio,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            ejercicioService.cambiarEstado(idEjercicio, authentication.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Estado del ejercicio actualizado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ejercicio/listado";
    }
}