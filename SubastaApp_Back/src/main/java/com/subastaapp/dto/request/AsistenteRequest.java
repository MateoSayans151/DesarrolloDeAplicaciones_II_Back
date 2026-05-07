package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsistenteRequest {
    @NotNull
    private Integer numeroPostor;
    @NotNull
    private Long usuarioId;
}
