package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.DetalleVenta;
import com.gymsystem.gymsystem.domain.Producto;
import com.gymsystem.gymsystem.domain.Venta;
import com.gymsystem.gymsystem.repository.DetalleVentaRepository;
import com.gymsystem.gymsystem.repository.ProductoRepository;
import com.gymsystem.gymsystem.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public void registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getId_producto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalle.getId_producto()));

            if (producto.getExistencias() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + 
                                           " (Disponibles: " + producto.getExistencias() + ")");
            }
        }

        double totalGeneral = 0.0;
        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getId_producto()).get();
            
            detalle.setPrecio_unitario(producto.getPrecio());
            double subtotal = producto.getPrecio() * detalle.getCantidad();
            detalle.setSubtotal(subtotal);
            
            totalGeneral += subtotal;
        }

        venta.setFecha(LocalDateTime.now());
        venta.setTotal(totalGeneral);

        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : detalles) {
            detalle.setId_venta(ventaGuardada.getId_venta());
            detalleVentaRepository.save(detalle);

            Producto producto = productoRepository.findById(detalle.getId_producto()).get();
            producto.setExistencias(producto.getExistencias() - detalle.getCantidad());
            productoRepository.save(producto);
        }
    }
}