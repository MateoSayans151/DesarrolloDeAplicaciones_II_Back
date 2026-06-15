package com.subastaapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelProgressResponse {
    private String nivelActual;
    private int pujasGanadas;
    private Integer pujasParaSiguiente; // null si ya es el nivel máximo
    private double progreso;            // 0.0 a 1.0
    private String tier;                // comun | plata | oro | platino | especial
}
