package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PujaRequest {
    @NotNull
    private Long asistente;
    @NotNull
    private BigDecimal importe;
}
