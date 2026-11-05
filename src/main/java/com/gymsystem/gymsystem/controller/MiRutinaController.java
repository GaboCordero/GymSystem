package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.RutinaCliente;
import com.gymsystem.gymsystem.service.RutinaClienteService;
import com.gymsystem.gymsystem.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MiRutinaController {

    private final RutinaClienteService rutinaClienteService;
    private final RutinaService rutinaService;

    @GetMapping("/mi-rutina")
    public String miRutina(Model model, Authentication authentication) {

        RutinaCliente asignacion = rutinaClienteService.getRutinaActual(authentication.getName());

        model.addAttribute("asignacion", asignacion);

        if (asignacion != null) {
            model.addAttribute("detalles", rutinaService.getEjerciciosDeRutina(
                    asignacion.getRutina().getIdRutina()));
        }

        return "rutina/mi-rutina";
    }
}