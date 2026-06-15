package com.subastaapp.dto.response;

import com.subastaapp.model.Notificacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionResponse {
    private Long id;
    private String titulo;
    private String mensaje;
    private String categoriaDestino;
    private LocalDateTime fechaCreacion;

    public static NotificacionResponse from(Notificacion n) {
        NotificacionResponse r = new NotificacionResponse();
        r.setId(n.getId());
        r.setTitulo(n.getTitulo());
        r.setMensaje(n.getMensaje());
        r.setCategoriaDestino(n.getCategoriaDestino());
        r.setFechaCreacion(n.getFechaCreacion());
        return r;
    }
}
