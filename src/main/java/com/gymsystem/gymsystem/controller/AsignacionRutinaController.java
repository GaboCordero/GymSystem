package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.service.RutinaClienteService;
import com.gymsystem.gymsystem.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/asignacion")
public class AsignacionRutinaController {

    private final RutinaClienteService rutinaClienteService;
    private final RutinaService rutinaService;

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("asignaciones", rutinaClienteService.getAsignaciones(authentication.getName()));
        return "asignacion/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer idRutina,
            Model model, Authentication authentication) {

        model.addAttribute("clientes", rutinaClienteService.getClientesDisponibles(authentication.getName()));
        model.addAttribute("rutinas", rutinaService.getRutinasActivas(authentication.getName()));
        model.addAttribute("idRutinaSeleccionada", idRutina);

        return "asignacion/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idRutina,
            @RequestParam(required = false) String observaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            rutinaClienteService.asignarRutina(idCliente, idRutina,
                    observaciones, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Rutina asignada correctamente");

            return "redirect:/asignacion/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/asignacion/nuevo";
        }
    }
}