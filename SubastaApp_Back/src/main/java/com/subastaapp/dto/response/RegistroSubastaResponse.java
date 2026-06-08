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
    private String productoDescripcion;
    private Long compradorUsuarioId;
    private String compradorNombre;
    private BigDecimal importe;
    private BigDecimal comision;

    public static RegistroSubastaResponse from(RegistroSubasta r) {
        RegistroSubastaResponse res = new RegistroSubastaResponse();
        res.setIdentificador(r.getId());
        res.setSubasta(r.getSubasta().getId());
        res.setPropietarioUsuarioId(r.getPropietarioUsuario().getId());
        res.setProducto(r.getProducto().getId());
        res.setProductoDescripcion(r.getProducto().getDescripcionCompleta());
        if (r.getCompradorUsuario() != null) {
            res.setCompradorUsuarioId(r.getCompradorUsuario().getId());
            res.setCompradorNombre(r.getCompradorUsuario().getNombre() + " " + r.getCompradorUsuario().getApellido());
        } else {
            res.setCompradorNombre("La empresa");
        }
        res.setImporte(r.getImporte());
        res.setComision(r.getComision());
        return res;
    }
}
