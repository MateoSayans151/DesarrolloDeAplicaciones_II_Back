package com.subastaapp.repository;

import com.subastaapp.model.ItemCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCatalogoRepository extends JpaRepository<ItemCatalogo, Long> {
    List<ItemCatalogo> findByCatalogoId(Long catalogoId);
    boolean existsByCatalogoIdAndProductoId(Long catalogoId, Long productoId);
}
