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
public void aprobarProducto(Long id) {
Producto producto = productoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

producto.setEstado(Producto.EstadoProducto.ACEPTADO);
productoRepository.save(producto);

// Simular tasación: Asignar un precio base aleatorio al item vinculado
itemCatalogoRepository.findByProductoId(id).stream().findFirst().ifPresent(item -> {
    double randomPrice = 1000 + (new java.util.Random().nextDouble() * 9000);
    item.setPrecioBase(BigDecimal.valueOf(randomPrice).setScale(2, java.math.RoundingMode.HALF_UP));
    itemCatalogoRepository.save(item);
});
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
