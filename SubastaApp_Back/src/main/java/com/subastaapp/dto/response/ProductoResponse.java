package com.subastaapp.dto.response;

import com.subastaapp.model.Producto;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductoResponse {
    private Long id;
    private String descripcionCompleta;
    private Long propietarioUsuarioId;
    private String estado;
    private List<String> fotos;
    
    private String polizaSeguro;
    private String aseguradora;
    private java.math.BigDecimal montoAsegurado;
    private String monedaAsegurado;
    
    private String artista;
    private String disenador;
    private String historia;
    private String ubicacionDeposito;
    
    private Boolean origenLicitoDeclarado;
    private Boolean propietarioDeclarado;
    private String motivoRechazo;
    private java.math.BigDecimal precioPropuesto;

    public static ProductoResponse from(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setDescripcionCompleta(p.getDescripcionCompleta());
        r.setPropietarioUsuarioId(p.getPropietarioUsuario().getId());
        r.setEstado(p.getEstado().name());
        
        r.setPolizaSeguro(p.getPolizaSeguro());
        r.setAseguradora(p.getAseguradora());
        r.setMontoAsegurado(p.getMontoAsegurado());
        r.setMonedaAsegurado(p.getMonedaAsegurado());
        
        r.setArtista(p.getArtista());
        r.setDisenador(p.getDisenador());
        r.setHistoria(p.getHistoria());
        r.setUbicacionDeposito(p.getUbicacionDeposito());
        
        r.setOrigenLicitoDeclarado(p.getOrigenLicitoDeclarado());
        r.setPropietarioDeclarado(p.getPropietarioDeclarado());
        r.setMotivoRechazo(p.getMotivoRechazo());
        r.setPrecioPropuesto(p.getPrecioPropuesto());
        if (p.getFotos() != null) {
            r.setFotos(p.getFotos().stream().map(f -> f.getFotoBase64()).collect(Collectors.toList()));
        }

        return r;
    }
}
