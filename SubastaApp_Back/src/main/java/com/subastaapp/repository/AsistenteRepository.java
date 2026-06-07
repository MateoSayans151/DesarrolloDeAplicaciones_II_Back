package com.subastaapp.repository;

import com.subastaapp.model.Asistente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenteRepository extends JpaRepository<Asistente, Long> {
    boolean existsBySubastaIdAndUsuarioId(Long subastaId, Long usuarioId);
    List<Asistente> findBySubastaId(Long subastaId);
    List<Asistente> findByUsuarioId(Long usuarioId);
    void deleteBySubastaId(Long subastaId);
}
