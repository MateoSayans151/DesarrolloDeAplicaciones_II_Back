package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.PujaRequest;
import com.subastaapp.dto.response.PujaResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ForbiddenException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.*;
import com.subastaapp.repository.AsistenteRepository;
import com.subastaapp.repository.ItemCatalogoRepository;
import com.subastaapp.repository.PujaRepository;
import lombok.RequiredArgsConstructor;
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

        BigDecimal precioBase = item.getPrecioBase();
        Optional<Puja> mejorPujaOpt = pujaRepository.findTopByItemIdOrderByImporteDesc(itemId);

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
        return PujaResponse.from(pujaRepository.save(puja));
    }

    public List<PujaResponse> historial(Long itemId) {
        if (!itemCatalogoRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item no encontrado");
        }
        return pujaRepository.findByItemIdOrderByImporteDesc(itemId)
                .stream().map(PujaResponse::from).toList();
    }
}
