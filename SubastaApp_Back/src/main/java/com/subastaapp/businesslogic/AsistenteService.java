package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.AsistenteRequest;
import com.subastaapp.dto.response.AsistenteResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import java.util.List;
import com.subastaapp.model.Asistente;
import com.subastaapp.model.Subasta;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.AsistenteRepository;
import com.subastaapp.repository.SubastaRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsistenteService {

    private final AsistenteRepository asistenteRepository;
    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AsistenteResponse> listarPorSubasta(Long subastaId) {
        if (!subastaRepository.existsById(subastaId)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        return asistenteRepository.findBySubastaId(subastaId)
                .stream().map(AsistenteResponse::from).toList();
    }

    public AsistenteResponse registrar(Long subastaId, AsistenteRequest req) {
        Subasta subasta = subastaRepository.findById(subastaId)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        Usuario usuario = usuarioRepository.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (asistenteRepository.existsBySubastaIdAndUsuarioId(subastaId, req.getUsuarioId())) {
            throw new ConflictException("El usuario ya esta registrado como asistente en esta subasta");
        }

        // El usuario verificado puede participar segun su categoria
        if (usuario.getVerificado() != Usuario.EstadoVerificacion.si) {
            throw new IllegalArgumentException("El usuario no esta verificado para participar en subastas");
        }

        Asistente asistente = new Asistente();
        asistente.setNumeroPostor(req.getNumeroPostor());
        asistente.setUsuario(usuario);
        asistente.setSubasta(subasta);
        return AsistenteResponse.from(asistenteRepository.save(asistente));
    }
}
