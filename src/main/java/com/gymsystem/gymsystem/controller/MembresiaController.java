package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Membresia;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.service.ClienteService;
import com.gymsystem.gymsystem.service.MembresiaService;
import com.gymsystem.gymsystem.service.PagoService;
import com.gymsystem.gymsystem.service.PlanMembresiaService;
import com.gymsystem.gymsystem.service.SesionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/membresia")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final PlanMembresiaService planMembresiaService;
    private final PagoService pagoService;
    private final ClienteService clienteService;
    private final SesionService sesionService;

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        model.addAttribute("membresias", membresiaService.getMembresias(authentication.getName()));
        return "membresia/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer idCliente,
            Model model, Authentication authentication) {

        Usuario administrador = sesionService.obtenerUsuario(authentication.getName(), "ADMIN");

        model.addAttribute("clientes", clienteService.getClientesPorEmpresa(
                administrador.getEmpresa().getIdEmpresa())
                .stream()
                .filter(c -> c.getUsuario().isActivo())
                .toList());
        model.addAttribute("planes", planMembresiaService.getPlanesActivos(authentication.getName()));
        model.addAttribute("idClienteSeleccionado", idCliente);
        model.addAttribute("hoy", LocalDate.now());

        return "membresia/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idPlan,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Membresia membresia = membresiaService.asignarMembresia(
                    idCliente, idPlan, fechaInicio, authentication.getName());

            redirectAttributes.addFlashAttribute("mensaje",
                    "Membresía asignada correctamente. Registre el pago para activarla.");

            return "redirect:/membresia/detalle/" + membresia.getIdMembresia();

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return idCliente == null
                    ? "redirect:/membresia/nuevo"
                    : "redirect:/membresia/nuevo?idCliente=" + idCliente;
        }
    }

    @GetMapping("/detalle/{idMembresia}")
    public String detalle(@PathVariable Integer idMembresia, Model model,
            Authentication authentication) {

        Membresia membresia = membresiaService.getMembresia(idMembresia, authentication.getName());

        if (membresia == null) {
            return "redirect:/membresia/listado";
        }

        BigDecimal totalPagado = pagoService.getTotalPagado(idMembresia);

        model.addAttribute("membresia", membresia);
        model.addAttribute("pagos", pagoService.getPagosMembresia(idMembresia));
        model.addAttribute("totalPagado", totalPagado);
        model.addAttribute("saldo", membresia.getPrecio().subtract(totalPagado));
        model.addAttribute("metodosPago", PagoService.METODOS_PAGO);

        return "membresia/detalle";
    }
}