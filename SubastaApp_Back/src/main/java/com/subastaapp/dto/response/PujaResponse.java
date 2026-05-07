package com.subastaapp.dto.response;

import com.subastaapp.model.Puja;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PujaResponse {
    private Long identificador;
    private Long asistente;
    private Long item;
    private BigDecimal importe;
    private String ganador;

    public static PujaResponse from(Puja p) {
        PujaResponse r = new PujaResponse();
        r.setIdentificador(p.getId());
        r.setAsistente(p.getAsistente().getId());
        r.setItem(p.getItem().getId());
        r.setImporte(p.getImporte());
        r.setGanador(p.getGanador().name());
        return r;
    }
}
