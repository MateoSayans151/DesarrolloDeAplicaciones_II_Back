package com.subastaapp.repository;

import com.subastaapp.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubastaRepository extends JpaRepository<Subasta, Long> {
    List<Subasta> findByEstado(Subasta.EstadoSubasta estado);
}
