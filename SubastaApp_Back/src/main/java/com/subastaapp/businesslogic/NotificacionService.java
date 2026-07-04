package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.NotificacionRequest;
import com.subastaapp.dto.response.NotificacionResponse;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.Notificacion;
import com.subastaapp.model.NivelCategoria;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.NivelCategoriaRepository;
import com.subastaapp.repository.NotificacionRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NivelCategoriaRepository nivelCategoriaRepository;

    public NotificacionResponse enviarACategoria(String categoriaStr, NotificacionRequest req) {
        NivelCategoria nivel = nivelCategoriaRepository.findByNombre(categoriaStr)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría inválida: " + categoriaStr + ". Valores válidos: comun, plata, oro, platino, especial"));
        Notificacion notificacion = new Notificacion();
        notificacion.setTitulo(req.getTitulo());
        notificacion.setMensaje(req.getMensaje());
        notificacion.setCategoriaDestino(nivel.getNombre());
        return NotificacionResponse.from(notificacionRepository.save(notificacion));
    }

    public void notificarUsuario(Usuario destino, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setUsuarioDestino(destino);
        notificacionRepository.save(notificacion);
    }

    public List<NotificacionResponse> listarParaUsuario(String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<Notificacion> porCategoria = usuario.getNivelCategoria() != null
                ? notificacionRepository.findByCategoriaDestinoOrderByFechaCreacionDesc(usuario.getNivelCategoria().getNombre())
                : List.of();
        List<Notificacion> dirigidas = notificacionRepository.findByUsuarioDestinoIdOrderByFechaCreacionDesc(usuario.getId());
        return Stream.concat(porCategoria.stream(), dirigidas.stream())
                .sorted(Comparator.comparing(Notificacion::getFechaCreacion).reversed())
                .map(NotificacionResponse::from)
                .toList();
    }
}
