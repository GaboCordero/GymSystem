package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.DetalleVenta;
import com.gymsystem.gymsystem.domain.Venta;
import com.gymsystem.gymsystem.service.ProductoService;
import com.gymsystem.gymsystem.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/venta")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    @GetMapping("/listado")
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaService.obtenerTodas());
        return "venta/listado";
    }

    @GetMapping("/nuevo")
    public String nuevaVenta(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "venta/modifica";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute Venta venta,
                               @RequestParam List<Integer> idProductos,
                               @RequestParam List<Integer> cantidades) {
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        for (int i = 0; i < idProductos.size(); i++) {
            if (cantidades.get(i) != null && cantidades.get(i) > 0) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId_producto(idProductos.get(i));
                detalle.setCantidad(cantidades.get(i));
                detalles.add(detalle);
            }
        }

        try {
            ventaService.registrarVenta(venta, detalles);
        } catch (RuntimeException e) {
            return "redirect:/venta/nuevo?error=" + e.getMessage();
        }

        return "redirect:/venta/listado";
    }
}