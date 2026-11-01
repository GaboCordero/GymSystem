package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.gymsystem.gymsystem.service.EmpresaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    @InitBinder
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields(
                "nombre",
                "apellidos",
                "username",
                "correo",
                "telefono",
                "password"
        );
    }

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {

        Usuario usuarioActual = usuarioService.getUsuarioPorUsername(
                authentication.getName());

        boolean superadmin = authentication.getAuthorities().stream()
                .anyMatch(autoridad
                        -> autoridad.getAuthority().equals("ROLE_SUPERADMIN"));

        List<Usuario> usuarios;

        if (superadmin) {
            usuarios = usuarioService.getUsuarios();
        } else {
            if (usuarioActual == null || usuarioActual.getEmpresa() == null) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Acceso no autorizado");
            }

            usuarios = usuarioService.getUsuariosPorEmpresa(
                    usuarioActual.getEmpresa().getIdEmpresa());
        }

        model.addAttribute("usuarios", usuarios);

        model.addAttribute("superadmin", superadmin);
        model.addAttribute("idUsuarioActual", usuarioActual.getIdUsuario());

        return "usuario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication) {

        Usuario usuarioActual = usuarioService.getUsuarioPorUsername(
                authentication.getName());

        boolean superadmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("superadmin", superadmin);

        if (superadmin) {
            model.addAttribute("empresas", empresaService.getEmpresas());
        }

        return "usuario/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(Usuario usuario,
            @RequestParam String rol,
            @RequestParam(required = false) Integer idEmpresa,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        boolean superadmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        try {
            usuarioService.registrarUsuario(
                    usuario,
                    rol,
                    idEmpresa,
                    authentication.getName(),
                    superadmin
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Usuario registrado correctamente");

            return "redirect:/usuario/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/usuario/nuevo";
        }
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario,
            Model model, Authentication authentication) {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario = usuarioService.getUsuario(usuario);

        if (usuario == null) {
            return "redirect:/usuario/listado";
        }

        Usuario administrador = usuarioService.getUsuarioPorUsername(
                authentication.getName());

        boolean superadmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean usuarioAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getRol().equals("ADMIN"));

        if (superadmin) {
            if (!usuarioAdmin || usuario.getEmpresa() == null) {
                throw new AccessDeniedException("Acceso no autorizado");
            }
        } else if (admin) {
            if (administrador == null
                    || administrador.getEmpresa() == null
                    || !administrador.getEmpresa().isActivo()
                    || usuario.getEmpresa() == null
                    || !administrador.getEmpresa().getIdEmpresa()
                            .equals(usuario.getEmpresa().getIdEmpresa())
                    || usuarioAdmin) {
                throw new AccessDeniedException("Acceso no autorizado");
            }
        } else {
            throw new AccessDeniedException("Acceso no autorizado");
        }

        Usuario datos = new Usuario();
        datos.setIdUsuario(usuario.getIdUsuario());
        datos.setNombre(usuario.getNombre());
        datos.setApellidos(usuario.getApellidos());
        datos.setUsername(usuario.getUsername());
        datos.setCorreo(usuario.getCorreo());
        datos.setTelefono(usuario.getTelefono());

        model.addAttribute("usuario", datos);
        model.addAttribute("edicion", true);

        return "usuario/modifica";
    }

    @PostMapping("/actualizar/{idUsuario}")
    public String actualizar(@PathVariable Integer idUsuario,
            @ModelAttribute Usuario usuario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.modificarUsuario(
                    idUsuario,
                    usuario,
                    authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Usuario actualizado correctamente");

            return "redirect:/usuario/listado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/usuario/modificar/" + idUsuario;
        }
    }

    @PostMapping("/estado/{idUsuario}")
    public String cambiarEstado(@PathVariable Integer idUsuario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.cambiarEstado(idUsuario, authentication.getName());

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Estado del usuario actualizado correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/usuario/listado";
    }

}
