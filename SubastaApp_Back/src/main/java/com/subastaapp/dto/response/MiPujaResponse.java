package com.subastaapp.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MiPujaResponse {
    private Long pujaId;
    private BigDecimal importe;
    private BigDecimal mejorImporte;
    private boolean esMejorPuja;
    private Long itemId;
    private BigDecimal precioBase;
    private String productoDescripcion;
    private Long subastaId;
    private LocalDate subastaFecha;
    private String subastaEstado;
}
