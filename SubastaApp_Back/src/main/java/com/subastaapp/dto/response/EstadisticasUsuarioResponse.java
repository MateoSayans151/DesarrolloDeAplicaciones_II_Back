package com.subastaapp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EstadisticasUsuarioResponse {
    private Long usuarioId;
    private String categoria;
    private long subastasCreadas;
    private long productosSubidos;
    private long productosVendidos;
    private long pujasGanadas;
    private BigDecimal montoTotalGastado;
}
