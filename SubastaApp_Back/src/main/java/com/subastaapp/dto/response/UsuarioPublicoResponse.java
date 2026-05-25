package com.subastaapp.dto.response;

import com.subastaapp.model.Usuario;
import lombok.Data;

@Data
public class UsuarioPublicoResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String fotoBase64;
    private Usuario.CategoriaUsuario categoria;

    public static UsuarioPublicoResponse from(Usuario u) {
        UsuarioPublicoResponse r = new UsuarioPublicoResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setApellido(u.getApellido());
        r.setFotoBase64(u.getFotoBase64());
        r.setCategoria(u.getCategoria());
        return r;
    }
}