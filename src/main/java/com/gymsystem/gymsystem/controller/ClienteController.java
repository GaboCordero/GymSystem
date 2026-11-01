package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Cliente;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.service.ClienteService;
import com.gymsystem.gymsystem.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {

        Usuario administrador = obtenerAdministrador(authentication);

        List<Cliente> clientes = clienteService.getClientesPorEmpresa(
                administrador.getEmpresa().getIdEmpresa());

        model.addAttribute("clientes", clientes);

        return "cliente/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication) {

        Usuario administrador = obtenerAdministrador(authentication);

        List<Usuario> usuarios = usuarioService.getUsuariosPorEmpresa(
                administrador.getEmpresa().getIdEmpresa());

        usuarios = usuarios.stream()
                .filter(u -> u.isActivo())
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getRol().equals("CLIENTE")))
                .filter(u -> !clienteService.existeCliente(u.getIdUsuario()))
                .toList();

        model.addAttribute("usuarios", usuarios);

        return "cliente/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idUsuario,
            @RequestParam(required = false) String observaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        obtenerAdministrador(authentication);

        try {
            clienteService.registrarCliente(
                    idUsuario,
                    observaciones,
                    authentication.getName());

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Cliente registrado correctamente");

            return "redirect:/cliente/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/cliente/nuevo";
        }
    }

    private Usuario obtenerAdministrador(Authentication authentication) {

        Usuario administrador = usuarioService.getUsuarioPorUsername(
                authentication.getName());

        boolean tieneRol = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!tieneRol || administrador == null
                || !administrador.isActivo()
                || administrador.getEmpresa() == null
                || !administrador.getEmpresa().isActivo()) {
            throw new AccessDeniedException("Acceso no autorizado");
        }

        return administrador;
    }
}
