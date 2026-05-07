package com.subastaapp.repository;

import com.subastaapp.model.RegistroSubasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroSubastaRepository extends JpaRepository<RegistroSubasta, Long> {
    List<RegistroSubasta> findBySubastaId(Long subastaId);
}
