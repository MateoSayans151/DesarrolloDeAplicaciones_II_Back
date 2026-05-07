package com.subastaapp.dto.response;

import com.subastaapp.model.Catalogo;
import lombok.Data;

@Data
public class CatalogoResponse {
    private Long id;
    private String descripcion;
    private Long subasta;

    public static CatalogoResponse from(Catalogo c) {
        CatalogoResponse r = new CatalogoResponse();
        r.setId(c.getId());
        r.setDescripcion(c.getDescripcion());
        r.setSubasta(c.getSubasta() != null ? c.getSubasta().getId() : null);
        return r;
    }
}
