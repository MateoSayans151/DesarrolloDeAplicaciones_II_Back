package com.subastaapp.dto.request;

import com.subastaapp.model.Usuario;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioVerificacionRequest {
    @NotNull
    private Usuario.EstadoVerificacion verificado;
    private Usuario.EstadoVerificacion verificacionFinanciera;
    private Usuario.EstadoVerificacion verificacionJudicial;
    @Min(1) @Max(6)
    private Integer calificacionRiesgo;
}
