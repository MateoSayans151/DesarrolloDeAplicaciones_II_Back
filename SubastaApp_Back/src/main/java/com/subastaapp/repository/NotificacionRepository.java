package com.subastaapp.repository;

import com.subastaapp.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByCategoriaDestinoOrderByFechaCreacionDesc(String categoriaDestino);
}
