package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.service.PagoService;
import java.math.BigDecimal;
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
@RequestMapping("/pago")
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("pagos", pagoService.getPagos(authentication.getName()));
        return "pago/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idMembresia,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String referencia,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            pagoService.registrarPago(idMembresia, monto, metodoPago,
                    referencia, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Pago registrado correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/membresia/detalle/" + idMembresia;
    }
}