package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Producto;
import com.gymsystem.gymsystem.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/listado")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "producto/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/modifica";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.obtenerPorId(id).orElse(null);

        if (producto == null) {
            return "redirect:/producto/listado";
        }

        model.addAttribute("producto", producto);
        return "producto/modifica";
    }
    
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/producto/listado";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Integer id) {
        productoService.cambiarEstado(id);
        return "redirect:/producto/listado";
    }
}
