package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.AsistenteRequest;
import com.subastaapp.dto.response.AsistenteResponse;
import com.subastaapp.dto.response.SubastaResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ForbiddenException;
import com.subastaapp.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.subastaapp.exception.UnauthorizedException;
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

    public List<SubastaResponse> listarSubastasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return asistenteRepository.findByUsuarioId(usuarioId)
                .stream().map(a -> SubastaResponse.from(a.getSubasta())).toList();
    }

    public List<AsistenteResponse> listarPorSubasta(Long subastaId) {
        if (!subastaRepository.existsById(subastaId)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        return asistenteRepository.findBySubastaId(subastaId)
                .stream().map(AsistenteResponse::from).toList();
    }

    public AsistenteResponse registrar(Long subastaId, AsistenteRequest req, String documento) {
        Subasta subasta = subastaRepository.findById(subastaId)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        Usuario usuario = usuarioRepository.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Usuario usuarioDoc = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
        if (usuarioDoc.getId() != usuario.getId()) {
            throw new UnauthorizedException("El id del usuario no corresponde con el de la solicitud");
        }

        if (asistenteRepository.existsBySubastaIdAndUsuarioId(subastaId, req.getUsuarioId())) {
            throw new ConflictException("El usuario ya esta registrado como asistente en esta subasta");
        }

        // El usuario verificado puede participar segun su categoria
        if (usuario.getVerificado() != Usuario.EstadoVerificacion.si) {
            throw new IllegalArgumentException("El usuario no esta verificado para participar en subastas");
        }
        //esto es un papelón. Habría que hacer un enum comun para ambas clases pero tienen muchas dependencias
        Map<String, Integer> categorias = new HashMap<>();
        categorias.put("comun", 1);
        categorias.put("especial", 2);
        categorias.put("plata", 3);
        categorias.put("oro", 4);
        categorias.put("platino", 5);
        System.out.println("categoria subasta: " + subasta.getCategoria());
        System.out.println("categoria usuario: " + usuario.getNivelCategoria());
        int subasta_rank = categorias.get(subasta.getCategoria().name());
        int user_rank = categorias.get(usuario.getNivelCategoria() != null ? usuario.getNivelCategoria().getNombre() : "comun");
        if(subasta_rank>user_rank){
            throw new ForbiddenException("La subasta es de categoría más alta");
        }

        Asistente asistente = new Asistente();
        asistente.setNumeroPostor(req.getNumeroPostor());
        asistente.setUsuario(usuario);
        asistente.setSubasta(subasta);
        return AsistenteResponse.from(asistenteRepository.save(asistente));
    }
}
