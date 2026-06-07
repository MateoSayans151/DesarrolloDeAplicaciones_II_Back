package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.CatalogoRequest;
import com.subastaapp.dto.request.ItemCatalogoRequest;
import com.subastaapp.dto.response.CatalogoDetalleResponse;
import com.subastaapp.dto.response.CatalogoResponse;
import com.subastaapp.dto.response.ItemCatalogoDetalleResponse;
import com.subastaapp.dto.response.ItemCatalogoResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ForbiddenException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.mapper.CatalogoProductoMapper;
import com.subastaapp.model.Catalogo;
import com.subastaapp.model.ItemCatalogo;
import com.subastaapp.model.Producto;
import com.subastaapp.model.Subasta;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.CatalogoRepository;
import com.subastaapp.repository.ItemCatalogoRepository;
import com.subastaapp.repository.ProductoRepository;
import com.subastaapp.repository.PujaRepository;
import com.subastaapp.repository.SubastaRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;
    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final ProductoRepository productoRepository;
    private final CatalogoProductoMapper catalogoProductoMapper;
    private final PujaRepository pujaRepository;

    public List<CatalogoResponse> listarTodos() {
        return catalogoRepository.findAll()
                .stream().map(CatalogoResponse::from).toList();
    }

    public ItemCatalogoResponse obtenerItem(Long itemId) {
        return ItemCatalogoResponse.from(itemCatalogoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado")));
    }

    public CatalogoResponse crear(CatalogoRequest req, String documento) {
        Usuario creador = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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

    /*
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
    */


    public CatalogoDetalleResponse obtenerCatalogoDetalle(Long subasta_id, boolean auth_user) {
        if (!subastaRepository.existsById(subasta_id)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        //buscamos el catalago
        Catalogo catalogo = catalogoRepository.findBySubastaId(subasta_id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));

        CatalogoDetalleResponse detalle = new CatalogoDetalleResponse();
        detalle.setId(catalogo.getId());
        detalle.setDescripcion(catalogo.getDescripcion());
        detalle.setSubasta(catalogo.getSubasta() != null ? catalogo.getSubasta().getId() : null);

        List<ItemCatalogoDetalleResponse> itemsResponse = new ArrayList<>();

        //obtener los itemscatalogo que referencian a este catalogo
        List<ItemCatalogo> items = itemCatalogoRepository.findByCatalogoId(catalogo.getId());
        for (ItemCatalogo item : items) {
            itemsResponse.add(catalogoProductoMapper.toDetalleResponse(item, auth_user));
        }

        detalle.setItems(itemsResponse);

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

    public void removerItem(Long catalogoId, Long itemId) {
        ItemCatalogo item = itemCatalogoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
        if (!item.getCatalogo().getId().equals(catalogoId)) {
            throw new ForbiddenException("El item no pertenece a este catálogo");
        }
        Catalogo catalogo = item.getCatalogo();
        if (catalogo.getSubasta() != null &&
                Subasta.EstadoSubasta.abierta.equals(catalogo.getSubasta().getEstado())) {
            throw new ConflictException("No se puede modificar el catálogo de una subasta abierta");
        }
        pujaRepository.deleteByItemId(itemId);
        itemCatalogoRepository.delete(item);
    }

    public ItemCatalogoResponse agregarItem(Long catalogoId, ItemCatalogoRequest req) {
        Catalogo catalogo = catalogoRepository.findById(catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        if (catalogo.getSubasta() != null &&
                Subasta.EstadoSubasta.abierta.equals(catalogo.getSubasta().getEstado())) {
            throw new ConflictException("No se puede modificar el catálogo de una subasta abierta");
        }
        Producto producto = productoRepository.findById(req.getProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (itemCatalogoRepository.existsByProductoId(req.getProducto())) {
            throw new ConflictException("El producto ya está asignado a otra subasta");
        }
        ItemCatalogo item = new ItemCatalogo();
        item.setCatalogo(catalogo);
        item.setProducto(producto);
        item.setPrecioBase(req.getPrecioBase());
        item.setComision(req.getComision());
        item.setSubastado(ItemCatalogo.subastado_bool.no);
        return ItemCatalogoResponse.from(itemCatalogoRepository.save(item));
    }
}
