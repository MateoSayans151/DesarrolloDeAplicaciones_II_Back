package com.subastaapp.dto.response;

import com.subastaapp.model.Producto;
import lombok.Data;

@Data
public class ProductoResponse {
    private Long id;
    private String descripcionCompleta;
    private Long propietarioUsuarioId;
    private String estado;
    
    private String polizaSeguro;
    private String aseguradora;
    private java.math.BigDecimal montoAsegurado;
    
    private String artista;
    private String disenador;
    private String historia;
    private String ubicacionDeposito;
    
    private Boolean origenLicitoDeclarado;
    private Boolean propietarioDeclarado;
    private String motivoRechazo;

    public static ProductoResponse from(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setDescripcionCompleta(p.getDescripcionCompleta());
        r.setPropietarioUsuarioId(p.getPropietarioUsuario().getId());
        r.setEstado(p.getEstado().name());
        
        r.setPolizaSeguro(p.getPolizaSeguro());
        r.setAseguradora(p.getAseguradora());
        r.setMontoAsegurado(p.getMontoAsegurado());
        
        r.setArtista(p.getArtista());
        r.setDisenador(p.getDisenador());
        r.setHistoria(p.getHistoria());
        r.setUbicacionDeposito(p.getUbicacionDeposito());
        
        r.setOrigenLicitoDeclarado(p.getOrigenLicitoDeclarado());
        r.setPropietarioDeclarado(p.getPropietarioDeclarado());
        r.setMotivoRechazo(p.getMotivoRechazo());
        
        return r;
    }
}
