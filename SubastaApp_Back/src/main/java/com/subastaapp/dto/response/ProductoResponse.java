package com.subastaapp.dto.response;

import com.subastaapp.model.Producto;
import lombok.Data;

@Data
public class ProductoResponse {
    private Long id;
    private String descripcionCatalogo;
    private String descripcionCompleta;
    private Long propietarioUsuarioId;

    public static ProductoResponse from(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setDescripcionCatalogo(p.getDescripcionCatalogo());
        r.setDescripcionCompleta(p.getDescripcionCompleta());
        r.setPropietarioUsuarioId(p.getPropietarioUsuario().getId());
        return r;
    }
}
