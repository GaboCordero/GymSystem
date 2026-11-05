package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.PlanMembresia;
import com.gymsystem.gymsystem.service.PlanMembresiaService;
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
@RequestMapping("/plan")
public class PlanMembresiaController {

    private final PlanMembresiaService planMembresiaService;

    @InitBinder("plan")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idPlan", "nombre", "descripcion", "duracionDias", "precio");
    }

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("planes", planMembresiaService.getPlanes(authentication.getName()));
        return "plan/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("plan")) {
            model.addAttribute("plan", new PlanMembresia());
        }
        return "plan/modifica";
    }

    @GetMapping("/modificar/{idPlan}")
    public String modificar(@PathVariable Integer idPlan, Model model,
            Authentication authentication) {

        if (!model.containsAttribute("plan")) {
            PlanMembresia plan = planMembresiaService.getPlan(idPlan, authentication.getName());

            if (plan == null) {
                return "redirect:/plan/listado";
            }

            model.addAttribute("plan", plan);
        }

        return "plan/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("plan") PlanMembresia plan,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            planMembresiaService.guardar(plan, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje", "Plan guardado correctamente");

            return "redirect:/plan/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("plan", plan);

            return plan.getIdPlan() == null
                    ? "redirect:/plan/nuevo"
                    : "redirect:/plan/modificar/" + plan.getIdPlan();
        }
    }

    @PostMapping("/estado/{idPlan}")
    public String cambiarEstado(@PathVariable Integer idPlan,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            planMembresiaService.cambiarEstado(idPlan, authentication.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Estado del plan actualizado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/plan/listado";
    }
}