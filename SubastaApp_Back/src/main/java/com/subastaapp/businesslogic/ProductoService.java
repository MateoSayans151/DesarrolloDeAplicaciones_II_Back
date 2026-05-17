package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.FotoRequest;
import com.subastaapp.dto.request.ProductoRequest;
import com.subastaapp.dto.response.ProductoResponse;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.Catalogo;
import com.subastaapp.model.Foto;
import com.subastaapp.model.ItemCatalogo;
import com.subastaapp.model.Producto;
import com.subastaapp.model.Usuario;
import com.subastaapp.repository.CatalogoRepository;
import com.subastaapp.repository.FotoRepository;
import com.subastaapp.repository.ItemCatalogoRepository;
import com.subastaapp.repository.ProductoRepository;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final FotoRepository fotoRepository;

    @org.springframework.transaction.annotation.Transactional
    public ProductoResponse crearEnCatalogo(Long catalogoId, ProductoRequest req, String documento) {
        Catalogo catalogo = catalogoRepository.findById(catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        
        Usuario propietario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Producto producto = new Producto();
        producto.setFecha(req.getFecha());
        producto.setDisponible(req.getDisponible() != null ? req.getDisponible() : Producto.DisponibilidadProducto.si);
        producto.setDescripcionCatalogo(req.getDescripcionCatalogo());
        producto.setDescripcionCompleta(req.getDescripcionCompleta());
        producto.setPropietarioUsuario(propietario);
        producto.setSeguro(req.getSeguro());
        Producto productoGuardado = productoRepository.save(producto);

        // Vincula automaticamente el producto al catalogo con valores por defecto
        ItemCatalogo item = new ItemCatalogo();
        item.setCatalogo(catalogo);
        item.setProducto(productoGuardado);
        item.setPrecioBase(BigDecimal.ZERO);
        item.setComision(BigDecimal.ZERO);
        itemCatalogoRepository.save(item);

        return ProductoResponse.from(productoGuardado);
    }

    public void agregarFoto(Long productoId, FotoRequest req) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Foto foto = new Foto();
        foto.setProducto(producto);
        foto.setFotoBase64(req.getFotoBase64());
        fotoRepository.save(foto);
    }
}
