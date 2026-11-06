package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Membresia;
import com.gymsystem.gymsystem.domain.Notificacion;
import com.gymsystem.gymsystem.repository.MembresiaRepository;
import com.gymsystem.gymsystem.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoService pagoService;

    @Transactional
    public void generarAviso() {
        List<Membresia> membresias = membresiaRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Membresia m : membresias) {
            if (Membresia.VENCIDA.equals(m.getEstado())) {
                continue;
            }

            BigDecimal totalPagadoBigDecimal = pagoService.getTotalPagado(m.getIdMembresia());
            double totalPagado = totalPagadoBigDecimal != null ? totalPagadoBigDecimal.doubleValue() : 0.0;
            double costoTotal = m.getPrecio() != null ? m.getPrecio().doubleValue() : 0.0;

            Integer idUsuarioCliente = m.getCliente().getIdCliente(); 

            if (totalPagado < costoTotal || Membresia.PENDIENTE.equals(m.getEstado())) {
                crearNotificacion(
                    idUsuarioCliente,
                    "PAGO_PENDIENTE",
                    "Posee un saldo pendiente, ya pagó: " + totalPagado + " de: " + costoTotal
                );
            }

            if (m.getFechaFin() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(hoy, m.getFechaFin());
                if (diasRestantes >= 0 && diasRestantes <= 3) {
                    crearNotificacion(
                        idUsuarioCliente,
                        "PROXIMO_VENCIMIENTO",
                        "Su membresía vence el: " + m.getFechaFin()
                    );
                }
            }
        }
    }

    private void crearNotificacion(Integer idUsuario, String tipo, String mensaje) {
        Notificacion notif = new Notificacion();
        notif.setIdUsuario(idUsuario);
        notif.setTipo(tipo);
        notif.setMensaje(mensaje);
        notif.setFecha(LocalDateTime.now());
        notif.setLeida(false);
        notificacionRepository.save(notif);
    }

    public List<Notificacion> obtenerNotificacionesPorUsuario(Integer idUsuario) {
        return notificacionRepository.findByIdUsuario(idUsuario);
    }
}
