package com.subastaapp.dto.response;

import com.subastaapp.model.RegistroSubasta;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegistroSubastaResponse {
    private Long identificador;
    private Long subasta;
    private Long propietarioUsuarioId;
    private Long producto;
    private Long compradorUsuarioId;
    private BigDecimal importe;
    private BigDecimal comision;

    public static RegistroSubastaResponse from(RegistroSubasta r) {
        RegistroSubastaResponse res = new RegistroSubastaResponse();
        res.setIdentificador(r.getId());
        res.setSubasta(r.getSubasta().getId());
        res.setPropietarioUsuarioId(r.getPropietarioUsuario().getId());
        res.setProducto(r.getProducto().getId());
        res.setCompradorUsuarioId(r.getCompradorUsuario() != null ? r.getCompradorUsuario().getId() : null);
        res.setImporte(r.getImporte());
        res.setComision(r.getComision());
        return res;
    }
}
