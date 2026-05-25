package com.subastaapp.mapper;

import com.subastaapp.dto.response.ItemCatalogoDetalleResponse;
import com.subastaapp.dto.response.ProductoResponse;
import com.subastaapp.model.ItemCatalogo;
import org.springframework.stereotype.Component;

@Component
public class CatalogoProductoMapper {

    public ItemCatalogoDetalleResponse toDetalleResponse(ItemCatalogo i, boolean auth_user) {
        ItemCatalogoDetalleResponse r = new ItemCatalogoDetalleResponse();
        r.setId(i.getId());
        r.setCatalogo(i.getCatalogo().getId());
        r.setComision(i.getComision());
        r.setDescripcionCatalogo(i.getDescripcionCatalogo());
        if (auth_user) {
            r.setPrecioBase(i.getPrecioBase());
        }
        else{
            r.setPrecioBase(null);
        }
        r.setProducto(ProductoResponse.from(i.getProducto()));
        return r;
    }
}
