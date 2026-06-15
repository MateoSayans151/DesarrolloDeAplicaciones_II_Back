package com.subastaapp.repository;

import com.subastaapp.model.Puja;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {
    List<Puja> findByItemIdOrderByImporteDesc(Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Puja> findTopByItemIdOrderByImporteDesc(Long itemId);

    List<Puja> findByAsistenteUsuarioId(Long usuarioId);
    List<Puja> findByAsistenteUsuarioIdAndGanador(Long usuarioId, Puja.EstadoGanador ganador);
    long countByAsistenteUsuarioIdAndGanador(Long usuarioId, Puja.EstadoGanador ganador);
    void deleteByItemId(Long itemId);
}
