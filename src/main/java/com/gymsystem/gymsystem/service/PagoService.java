package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Membresia;
import com.gymsystem.gymsystem.domain.Pago;
import com.gymsystem.gymsystem.domain.Usuario;
import com.gymsystem.gymsystem.repository.MembresiaRepository;
import com.gymsystem.gymsystem.repository.PagoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {

    public static final List<String> METODOS_PAGO
            = List.of("EFECTIVO", "TARJETA", "SINPE", "TRANSFERENCIA");

    private final PagoRepository pagoRepository;
    private final MembresiaRepository membresiaRepository;
    private final SesionService sesionService;

    @Transactional(readOnly = true)
    public List<Pago> getPagos(String usernameActual) {
        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");
        return pagoRepository.findByMembresia_Cliente_Usuario_Empresa_IdEmpresaOrderByFechaPagoDesc(
                administrador.getEmpresa().getIdEmpresa());
    }

    @Transactional(readOnly = true)
    public List<Pago> getPagosMembresia(Integer idMembresia) {
        return pagoRepository.findByMembresia_IdMembresiaOrderByFechaPagoDesc(idMembresia);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPagado(Integer idMembresia) {
        BigDecimal total = pagoRepository.sumarPagos(idMembresia);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Transactional
    public void registrarPago(Integer idMembresia, BigDecimal monto,
            String metodoPago, String referencia, String usernameActual) {

        Usuario administrador = sesionService.obtenerUsuario(usernameActual, "ADMIN");

        membresiaRepository.marcarVencidas(LocalDate.now());

        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));

        if (!sesionService.mismaEmpresa(administrador,
                membresia.getCliente().getUsuario().getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("La membresía no pertenece a su gimnasio");
        }

        if (Membresia.VENCIDA.equals(membresia.getEstado())) {
            throw new IllegalArgumentException("No se pueden registrar pagos a una membresía vencida");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0 || monto.scale() > 2) {
            throw new IllegalArgumentException("El monto no es válido");
        }

        if (metodoPago == null || !METODOS_PAGO.contains(metodoPago)) {
            throw new IllegalArgumentException("Seleccione un método de pago válido");
        }

        if (referencia != null && referencia.length() > 50) {
            throw new IllegalArgumentException("La referencia supera la longitud permitida");
        }

        BigDecimal totalPagado = getTotalPagado(idMembresia);
        BigDecimal saldo = membresia.getPrecio().subtract(totalPagado);

        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La membresía ya está pagada por completo");
        }

        if (monto.compareTo(saldo) > 0) {
            throw new IllegalArgumentException("El monto supera el saldo pendiente de ₡" + saldo);
        }

        Pago pago = new Pago();
        pago.setMembresia(membresia);
        pago.setUsuarioRegistro(administrador);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setReferencia(referencia == null || referencia.isBlank() ? null : referencia.trim());

        pagoRepository.save(pago);

        if (totalPagado.add(monto).compareTo(membresia.getPrecio()) >= 0) {
            membresia.setEstado(Membresia.ACTIVA);
            membresiaRepository.save(membresia);
        }
    }
}