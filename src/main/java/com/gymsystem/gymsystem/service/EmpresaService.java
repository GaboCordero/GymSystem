package com.gymsystem.gymsystem.service;

import com.gymsystem.gymsystem.domain.Empresa;
import com.gymsystem.gymsystem.repository.EmpresaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Empresa getEmpresa(Empresa empresa) {
        return empresaRepository.findById(empresa.getIdEmpresa()).orElse(null);
    }

    @Transactional
    public void save(Empresa empresa) {
        empresaRepository.save(empresa);
    }

}
