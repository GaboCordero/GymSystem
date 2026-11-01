package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Rol;
import com.gymsystem.gymsystem.repository.RolRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Rol getRol(String rol) {
        return rolRepository.findByRol(rol);
    }
}
