package com.gymsystem.gymsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Authentication authentication) {

        boolean superadmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (superadmin) {
            return "redirect:/empresa/listado";
        }

        if (admin) {
            return "redirect:/usuario/listado";
        }

        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String pantallaInicio() {
        return "inicio";
    }
}
