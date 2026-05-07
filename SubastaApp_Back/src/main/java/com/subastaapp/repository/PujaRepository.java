package com.subastaapp.repository;

import com.subastaapp.model.Puja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {
    List<Puja> findByItemIdOrderByImporteDesc(Long itemId);
    Optional<Puja> findTopByItemIdOrderByImporteDesc(Long itemId);
}
