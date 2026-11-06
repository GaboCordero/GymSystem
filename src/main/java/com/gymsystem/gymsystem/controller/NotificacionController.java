package com.gymsystem.gymsystem.controller;

import com.gymsystem.gymsystem.domain.Notificacion;
import com.gymsystem.gymsystem.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public String listarNotificaciones(Model model) {
        Integer idUsuarioActual = 1;

        notificacionService.generarAviso();

        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(idUsuarioActual);
        model.addAttribute("notificaciones", notificaciones);

        return "notificaciones/listado";
    }
}