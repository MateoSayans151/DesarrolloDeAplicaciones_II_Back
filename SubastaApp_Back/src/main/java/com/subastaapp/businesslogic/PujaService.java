package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.PujaRequest;
import com.subastaapp.dto.response.MiPujaResponse;
import com.subastaapp.dto.response.PujaResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ForbiddenException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.*;
import com.subastaapp.repository.AsistenteRepository;
import com.subastaapp.repository.ItemCatalogoRepository;
import com.subastaapp.repository.PujaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PujaService {

    private final PujaRepository pujaRepository;
    private final AsistenteRepository asistenteRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public PujaResponse pujar(Long itemId, PujaRequest req) {
        ItemCatalogo item = itemCatalogoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        Asistente asistente = asistenteRepository.findById(req.getAsistente())
                .orElseThrow(() -> new ResourceNotFoundException("Asistente no encontrado"));

        // Validar que el asistente pertenece a la misma subasta que el item
        Subasta subastaItem = item.getCatalogo().getSubasta();
        if (subastaItem == null || !subastaItem.getId().equals(asistente.getSubasta().getId())) {
            throw new ForbiddenException("El asistente no pertenece a la subasta de este item");
        }

        if (subastaItem.getEstado() != Subasta.EstadoSubasta.abierta) {
            throw new ConflictException("La subasta no esta abierta para pujas");
        }

        Usuario usuario = asistente.getUsuario();
        boolean tieneMedioVerificado = usuario.getMedioPagos().stream()
        .anyMatch(m -> m.getVerificado() == MedioPago.EstadoVerificacion.si);
        if (!tieneMedioVerificado) {
            throw new ForbiddenException("Necesitás un medio de pago verificado por la empresa para pujar.");
        }

        if (subastaItem.getItemActivoId() == null || subastaItem.getItemActivoId().longValue() != itemId) {
            throw new ConflictException("El articulo por el que queres pujar no se está subastando actualmente");
        }

        BigDecimal precioBase = item.getPrecioBase();
        Optional<Puja> mejorPujaOpt = pujaRepository.findTopByItemIdOrderByImporteDesc(itemId);

        if (mejorPujaOpt.isPresent() && mejorPujaOpt.get().getAsistente().getId().equals(asistente.getId())) {
            throw new ConflictException("Ya tenés la mejor oferta, no podés superarte a vos mismo");
        }

        BigDecimal mejorImporte = mejorPujaOpt.map(Puja::getImporte).orElse(precioBase);

        // Validacion de limites (no aplica para oro y platino)
        Subasta.CategoriaSubasta categoria = subastaItem.getCategoria();
        boolean aplicaLimites = categoria != Subasta.CategoriaSubasta.oro
                && categoria != Subasta.CategoriaSubasta.platino;

        if (aplicaLimites) {
            BigDecimal minimo = mejorImporte.add(precioBase.multiply(new BigDecimal("0.01")));
            BigDecimal maximo = mejorImporte.add(precioBase.multiply(new BigDecimal("0.20")));

            if (req.getImporte().compareTo(minimo) < 0) {
                throw new IllegalArgumentException(
                        "El importe debe ser al menos " + minimo.setScale(2, RoundingMode.HALF_UP));
            }
            if (req.getImporte().compareTo(maximo) > 0) {
                throw new IllegalArgumentException(
                        "El importe no puede superar " + maximo.setScale(2, RoundingMode.HALF_UP));
            }
        } else {
            if (req.getImporte().compareTo(mejorImporte) <= 0) {
                throw new IllegalArgumentException("El importe debe ser mayor a la mejor oferta actual");
            }
        }

        Puja puja = new Puja();
        puja.setAsistente(asistente);
        puja.setItem(item);
        puja.setImporte(req.getImporte());
        puja.setGanador(Puja.EstadoGanador.no);

        Puja pujaGuardada = pujaRepository.save(puja);
        PujaResponse response = PujaResponse.from(pujaGuardada);

        messagingTemplate.convertAndSend("/topic/items/" + itemId, response);
        return response;
    }

    public List<MiPujaResponse> listarMisPujas(Long usuarioId) {
        return pujaRepository.findByAsistenteUsuarioIdAndGanador(usuarioId, Puja.EstadoGanador.si).stream().map(p -> {
            BigDecimal mejorImporte = p.getImporte();
            MiPujaResponse r = new MiPujaResponse();
            r.setPujaId(p.getId());
            r.setImporte(p.getImporte());
            r.setMejorImporte(mejorImporte);
            r.setEsMejorPuja(p.getImporte().compareTo(mejorImporte) == 0);
            r.setItemId(p.getItem().getId());
            r.setPrecioBase(p.getItem().getPrecioBase());
            r.setProductoDescripcion(p.getItem().getProducto().getDescripcionCompleta());
            Subasta subasta = p.getItem().getCatalogo().getSubasta();
            if (subasta != null) {
                r.setSubastaId(subasta.getId());
                r.setSubastaFecha(subasta.getFecha());
                r.setSubastaEstado(subasta.getEstado().name());
            }
            return r;
        }).toList();
    }

    public List<PujaResponse> historial(Long itemId) {
        if (!itemCatalogoRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item no encontrado");
        }
        return pujaRepository.findByItemIdOrderByImporteDesc(itemId)
                .stream().map(PujaResponse::from).toList();
    }
}
