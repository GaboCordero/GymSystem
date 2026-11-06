package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Producto;
import com.gymsystem.gymsystem.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void cambiarEstado(Integer id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setActivo(!producto.getActivo());
            productoRepository.save(producto);
        }
    }
}