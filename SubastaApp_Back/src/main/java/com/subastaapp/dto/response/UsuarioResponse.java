package com.subastaapp.dto.response;

import com.subastaapp.model.Usuario;
import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String documento;
    private String nombre;
    private String direccion;
    private String fotoBase64;
    private Usuario.EstadoVerificacion verificado;
    private Usuario.CategoriaUsuario categoria;

    public static UsuarioResponse from(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setDocumento(u.getDocumento());
        r.setNombre(u.getNombre());
        r.setDireccion(u.getDireccion());
        r.setFotoBase64(u.getFotoBase64());
        r.setVerificado(u.getVerificado());
        r.setCategoria(u.getCategoria());
        return r;
    }
}
