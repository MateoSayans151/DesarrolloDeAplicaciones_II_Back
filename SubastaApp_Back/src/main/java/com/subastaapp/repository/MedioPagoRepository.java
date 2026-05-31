package com.subastaapp.repository;

import com.subastaapp.model.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {
    Optional<MedioPago> findById(Long id);
    Optional<List<MedioPago>> findAllByUsuarioId(Long usuarioId);
}
