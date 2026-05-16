package com.subastaapp.dto.response;

import com.subastaapp.model.Usuario;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UsuarioResponse {
    private Long id;
    private String documento;
    private String nombre;
    private String apellido;
    private String pais;
    private String domicilio;
    private String fotoBase64;
    private String fotoDocumentoFrente;
    private String fotoDocumentoDorso;
    private Usuario.EstadoVerificacion verificado;
    private Usuario.CategoriaUsuario categoria;
    private List<MedioPagoResponse> mediosPago;

    public static UsuarioResponse from(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setDocumento(u.getDocumento());
        r.setNombre(u.getNombre());
        r.setApellido(u.getApellido());
        r.setPais(u.getPais());
        r.setDomicilio(u.getDomicilio());
        r.setFotoBase64(u.getFotoBase64());
        r.setFotoDocumentoFrente(u.getFotoDocumentoFrente());
        r.setFotoDocumentoDorso(u.getFotoDocumentoDorso());
        r.setVerificado(u.getVerificado());
        r.setCategoria(u.getCategoria());

        if (u.getMedioPagos() != null) {
            r.setMediosPago(u.getMedioPagos().stream()
                    .map(MedioPagoResponse::from)
                    .collect(Collectors.toList()));
        }

        return r;
    }
}