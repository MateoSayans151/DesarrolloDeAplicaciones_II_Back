package com.subastaapp.repository;

import com.subastaapp.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {
}
