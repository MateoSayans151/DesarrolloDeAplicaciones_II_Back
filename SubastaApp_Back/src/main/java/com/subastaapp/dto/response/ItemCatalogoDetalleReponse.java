package com.subastaapp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCatalogoDetalleReponse {
    private Long id;
    private Long catalogo;
    private BigDecimal precioBase;
    private BigDecimal comision;
    private ProductoResponse producto;
}
