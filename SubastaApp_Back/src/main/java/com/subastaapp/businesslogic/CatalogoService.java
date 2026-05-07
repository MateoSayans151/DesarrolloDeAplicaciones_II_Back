package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.CatalogoRequest;
import com.subastaapp.dto.request.ItemCatalogoRequest;
import com.subastaapp.dto.response.CatalogoDetalleResponse;
import com.subastaapp.dto.response.CatalogoResponse;
import com.subastaapp.dto.response.ItemCatalogoResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.Catalogo;
import com.subastaapp.model.ItemCatalogo;
import com.subastaapp.model.Producto;
import com.subastaapp.model.Subasta;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.CatalogoRepository;
import com.subastaapp.repository.ItemCatalogoRepository;
import com.subastaapp.repository.ProductoRepository;
import com.subastaapp.repository.SubastaRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;
    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final ProductoRepository productoRepository;

    public CatalogoResponse crear(CatalogoRequest req) {
        Usuario creador = usuarioRepository.findById(req.getCreadorUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado"));
        Catalogo catalogo = new Catalogo();
        catalogo.setDescripcion(req.getDescripcion());
        catalogo.setCreadorUsuario(creador);
        if (req.getSubasta() != null) {
            Subasta subasta = subastaRepository.findById(req.getSubasta())
                    .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
            catalogo.setSubasta(subasta);
        }
        return CatalogoResponse.from(catalogoRepository.save(catalogo));
    }

    public CatalogoDetalleResponse obtenerDetalle(Long id) {
        Catalogo catalogo = catalogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        List<ItemCatalogoResponse> items = itemCatalogoRepository.findByCatalogoId(id)
                .stream().map(ItemCatalogoResponse::from).toList();
        CatalogoDetalleResponse detalle = new CatalogoDetalleResponse();
        detalle.setId(catalogo.getId());
        detalle.setDescripcion(catalogo.getDescripcion());
        detalle.setSubasta(catalogo.getSubasta() != null ? catalogo.getSubasta().getId() : null);
        detalle.setItems(items);
        return detalle;
    }

    public List<ItemCatalogoResponse> obtenerCatalogoPublico(Long subastaId) {
        if (!subastaRepository.existsById(subastaId)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        return catalogoRepository.findBySubastaId(subastaId)
                .map(c -> itemCatalogoRepository.findByCatalogoId(c.getId())
                        .stream().map(ItemCatalogoResponse::from).toList())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado para esta subasta"));
    }

    public ItemCatalogoResponse agregarItem(Long catalogoId, ItemCatalogoRequest req) {
        Catalogo catalogo = catalogoRepository.findById(catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        Producto producto = productoRepository.findById(req.getProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (itemCatalogoRepository.existsByCatalogoIdAndProductoId(catalogoId, req.getProducto())) {
            throw new ConflictException("El producto ya esta en este catalogo");
        }
        ItemCatalogo item = new ItemCatalogo();
        item.setCatalogo(catalogo);
        item.setProducto(producto);
        item.setPrecioBase(req.getPrecioBase());
        item.setComision(req.getComision());
        return ItemCatalogoResponse.from(itemCatalogoRepository.save(item));
    }
}
