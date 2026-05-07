package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.SubastaRequest;
import com.subastaapp.dto.response.RegistroSubastaResponse;
import com.subastaapp.dto.response.SubastaResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.*;
import com.subastaapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubastaService {

    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PujaRepository pujaRepository;
    private final RegistroSubastaRepository registroSubastaRepository;
    private final CatalogoRepository catalogoRepository;

    public SubastaResponse crear(SubastaRequest req) {
        Usuario creador = usuarioRepository.findById(req.getCreadorUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado"));
        Subasta subasta = new Subasta();
        subasta.setFecha(req.getFecha());
        subasta.setHora(req.getHora());
        subasta.setEstado(req.getEstado() != null ? req.getEstado() : Subasta.EstadoSubasta.cerrada);
        subasta.setCreadorUsuario(creador);
        subasta.setUbicacion(req.getUbicacion());
        subasta.setCapacidadAsistentes(req.getCapacidadAsistentes());
        subasta.setTieneDeposito(req.getTieneDeposito());
        subasta.setSeguridadPropia(req.getSeguridadPropia());
        subasta.setCategoria(req.getCategoria());
        return SubastaResponse.from(subastaRepository.save(subasta));
    }

    public List<SubastaResponse> listarAbiertas() {
        return subastaRepository.findByEstado(Subasta.EstadoSubasta.abierta)
                .stream().map(SubastaResponse::from).toList();
    }

    public SubastaResponse obtener(Long id) {
        return SubastaResponse.from(subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada")));
    }

    public void cambiarEstado(Long id, String estado) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        try {
            subasta.setEstado(Subasta.EstadoSubasta.valueOf(estado));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido: " + estado);
        }
        subastaRepository.save(subasta);
    }

    @Transactional
    public void cerrar(Long id) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        if (subasta.getEstado() == Subasta.EstadoSubasta.cerrada) {
            throw new ConflictException("La subasta ya se encuentra cerrada");
        }

        // Buscar el catalogo de la subasta
        catalogoRepository.findBySubastaId(id).ifPresent(catalogo -> {
            List<ItemCatalogo> items = itemCatalogoRepository.findByCatalogoId(catalogo.getId());
            for (ItemCatalogo item : items) {
                pujaRepository.findTopByItemIdOrderByImporteDesc(item.getId()).ifPresent(puja -> {
                    // Marcar puja ganadora
                    puja.setGanador(Puja.EstadoGanador.si);
                    pujaRepository.save(puja);

                    // Registrar resultado
                    RegistroSubasta registro = new RegistroSubasta();
                    registro.setSubasta(subasta);
                    registro.setPropietarioUsuario(item.getProducto().getPropietarioUsuario());
                    registro.setProducto(item.getProducto());
                    registro.setCompradorUsuario(puja.getAsistente().getUsuario());
                    registro.setImporte(puja.getImporte());
                    registro.setComision(item.getComision());
                    registroSubastaRepository.save(registro);
                });
            }
        });

        subasta.setEstado(Subasta.EstadoSubasta.cerrada);
        subastaRepository.save(subasta);
    }

    public List<RegistroSubastaResponse> obtenerRegistro(Long id) {
        if (!subastaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        return registroSubastaRepository.findBySubastaId(id)
                .stream().map(RegistroSubastaResponse::from).toList();
    }
}
