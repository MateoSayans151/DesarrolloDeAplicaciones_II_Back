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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final FotoRepository fotoRepository;

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll()
                .stream().map(ProductoResponse::from).toList();
    }

    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado")));
    }

    @org.springframework.transaction.annotation.Transactional
    public ProductoResponse crearEnCatalogo(Long catalogoId, ProductoRequest req, String documento) {
        Catalogo catalogo = catalogoRepository.findById(catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        
        Usuario propietario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Producto producto = new Producto();
        producto.setFecha(req.getFecha());
        producto.setEstado(Producto.EstadoProducto.PENDIENTE);
        producto.setDescripcionCompleta(req.getDescripcionCompleta());
        producto.setPropietarioUsuario(propietario);
        
        // Seteo de seguros
        producto.setPolizaSeguro(req.getPolizaSeguro());
        producto.setAseguradora(req.getAseguradora());
        producto.setMontoAsegurado(req.getMontoAsegurado());

        // Seteo de metadata
        producto.setArtista(req.getArtista());
        producto.setDisenador(req.getDisenador());
        producto.setHistoria(req.getHistoria());
        producto.setUbicacionDeposito(req.getUbicacionDeposito());

        // Seteo de declaraciones
        producto.setOrigenLicitoDeclarado(req.getOrigenLicitoDeclarado() != null && req.getOrigenLicitoDeclarado());
        producto.setPropietarioDeclarado(req.getPropietarioDeclarado() != null && req.getPropietarioDeclarado());

        Producto productoGuardado = productoRepository.save(producto);
// Vincula automaticamente el producto al catalogo con valores por defecto
ItemCatalogo item = new ItemCatalogo();
item.setCatalogo(catalogo);
item.setProducto(productoGuardado);
item.setPrecioBase(BigDecimal.ZERO);
item.setComision(BigDecimal.ZERO);
item.setSubastado(ItemCatalogo.subastado_bool.no);
itemCatalogoRepository.save(item);

return ProductoResponse.from(productoGuardado);
}

@org.springframework.transaction.annotation.Transactional
public void aprobarProducto(Long id, java.math.BigDecimal precioBase) {
    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    producto.setEstado(Producto.EstadoProducto.ACEPTADO);
    productoRepository.save(producto);

    // El administrador coloca el precio mínimo de puja (precio base)
    itemCatalogoRepository.findByProductoId(id).stream().findFirst().ifPresent(item -> {
        item.setPrecioBase(precioBase);
        item.setSubastado(ItemCatalogo.subastado_bool.no);
        itemCatalogoRepository.save(item);
    });
}
public List<ProductoResponse> listarPorUsuario(Long usuarioId) {
    if (!usuarioRepository.existsById(usuarioId)) {
        throw new ResourceNotFoundException("Usuario no encontrado");
    }
    return productoRepository.findByPropietarioUsuarioId(usuarioId)
            .stream().map(ProductoResponse::from).toList();
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
