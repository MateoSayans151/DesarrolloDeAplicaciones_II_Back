package com.subastaapp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCatalogoDetalleResponse {
    private Long id;
    private Long catalogo;
    private String descripcionCatalogo;
    private BigDecimal precioBase;
    private BigDecimal comision;
    private ProductoResponse producto;
}
