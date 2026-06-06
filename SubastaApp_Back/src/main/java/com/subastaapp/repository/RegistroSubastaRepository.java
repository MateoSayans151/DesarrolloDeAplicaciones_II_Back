package com.subastaapp.repository;

import com.subastaapp.model.RegistroSubasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RegistroSubastaRepository extends JpaRepository<RegistroSubasta, Long> {
    List<RegistroSubasta> findBySubastaId(Long subastaId);
    long countByPropietarioUsuarioId(Long usuarioId);
    long countByCompradorUsuarioId(Long usuarioId);
    @Query("SELECT COALESCE(SUM(r.importe), 0) FROM RegistroSubasta r WHERE r.compradorUsuario.id = :usuarioId")
    BigDecimal sumImporteByCompradorUsuarioId(Long usuarioId);
}
