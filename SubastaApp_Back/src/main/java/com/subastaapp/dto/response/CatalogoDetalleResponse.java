package com.subastaapp.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogoDetalleResponse extends CatalogoResponse {
    private List<ItemCatalogoDetalleReponse> items;
}
