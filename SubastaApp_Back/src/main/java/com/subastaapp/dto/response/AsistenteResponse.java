package com.subastaapp.dto.response;

import com.subastaapp.model.Asistente;
import lombok.Data;

@Data
public class AsistenteResponse {
    private Long identificador;
    private Integer numeroPostor;
    private Long usuarioId;
    private Long subasta;

    public static AsistenteResponse from(Asistente a) {
        AsistenteResponse r = new AsistenteResponse();
        r.setIdentificador(a.getId());
        r.setNumeroPostor(a.getNumeroPostor());
        r.setUsuarioId(a.getUsuario().getId());
        r.setSubasta(a.getSubasta().getId());
        return r;
    }
}
