package com.subastaapp.dto.response;

import com.subastaapp.model.ItemCatalogo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCatalogoResponse {
    private Long id;
    private Long catalogo;
    private Long producto;
    private String descripcionCatalogo;
    private BigDecimal precioBase;
    private BigDecimal comision;

    public static ItemCatalogoResponse from(ItemCatalogo i) {
        ItemCatalogoResponse r = new ItemCatalogoResponse();
        r.setId(i.getId());
        r.setCatalogo(i.getCatalogo().getId());
        r.setProducto(i.getProducto().getId());
        r.setDescripcionCatalogo(i.getDescripcionCatalogo());
        r.setPrecioBase(i.getPrecioBase());
        r.setComision(i.getComision());
        return r;
    }
}
