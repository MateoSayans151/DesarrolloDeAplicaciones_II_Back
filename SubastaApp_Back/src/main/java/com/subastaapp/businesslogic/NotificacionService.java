package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.NotificacionRequest;
import com.subastaapp.dto.response.NotificacionResponse;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.Notificacion;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.NotificacionRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionResponse enviarACategoria(String categoriaStr, NotificacionRequest req) {
        Usuario.CategoriaUsuario categoria;
        try {
            categoria = Usuario.CategoriaUsuario.valueOf(categoriaStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: " + categoriaStr + ". Valores válidos: comun, especial, plata, oro, platino");
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setTitulo(req.getTitulo());
        notificacion.setMensaje(req.getMensaje());
        notificacion.setCategoriaDestino(categoria);
        return NotificacionResponse.from(notificacionRepository.save(notificacion));
    }

    public List<NotificacionResponse> listarParaUsuario(String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (usuario.getCategoria() == null) {
            return List.of();
        }
        return notificacionRepository
                .findByCategoriaDestinoOrderByFechaCreacionDesc(usuario.getCategoria())
                .stream().map(NotificacionResponse::from).toList();
    }
}
