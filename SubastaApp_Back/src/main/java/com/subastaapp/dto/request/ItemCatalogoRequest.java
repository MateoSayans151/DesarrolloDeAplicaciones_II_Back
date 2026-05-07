package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCatalogoRequest {
    @NotNull
    private Long producto;
    @NotNull
    private BigDecimal precioBase;
    @NotNull
    private BigDecimal comision;
}
