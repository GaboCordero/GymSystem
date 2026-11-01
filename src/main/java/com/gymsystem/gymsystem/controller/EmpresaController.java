package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Empresa;
import com.gymsystem.gymsystem.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("empresas", empresaService.getEmpresas());
        return "empresa/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Empresa empresa) {
        return "empresa/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(Empresa empresa, RedirectAttributes redirectAttributes) {
        empresaService.save(empresa);
        redirectAttributes.addFlashAttribute("mensaje", "Empresa guardada correctamente");
        return "redirect:/empresa/listado";
    }

    @GetMapping("/modificar/{idEmpresa}")
    public String modificar(@PathVariable Integer idEmpresa, Model model) {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(idEmpresa);
        empresa = empresaService.getEmpresa(empresa);

        if (empresa == null) {
            return "redirect:/empresa/listado";
        }

        model.addAttribute("empresa", empresa);
        return "empresa/modifica";
    }

    @PostMapping("/estado/{idEmpresa}")
    public String cambiarEstado(@PathVariable Integer idEmpresa,
            RedirectAttributes redirectAttributes) {

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(idEmpresa);
        empresa = empresaService.getEmpresa(empresa);

        if (empresa != null) {
            empresa.setActivo(!empresa.isActivo());
            empresaService.save(empresa);
            redirectAttributes.addFlashAttribute("mensaje", "Estado de la empresa actualizado correctamente");
        }

        return "redirect:/empresa/listado";
    }
}
