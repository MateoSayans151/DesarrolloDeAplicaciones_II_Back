package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.FotoRequest;
import com.subastaapp.dto.request.ProductoRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.subastaapp.dto.response.ProductoResponse;
import com.subastaapp.exception.ConflictException;
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

    public ProductoResponse crear(ProductoRequest req, String documento) {
        Usuario propietario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Producto producto = buildProducto(req, propietario);
        return ProductoResponse.from(productoRepository.save(producto));
    }

    @org.springframework.transaction.annotation.Transactional
    public ProductoResponse crearEnCatalogo(Long catalogoId, ProductoRequest req, String documento) {
        Catalogo catalogo = catalogoRepository.findById(catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo no encontrado"));
        
        Usuario propietario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Producto productoGuardado = productoRepository.save(buildProducto(req, propietario));
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

    if (producto.getMontoAsegurado() != null && precioBase.compareTo(producto.getMontoAsegurado()) < 0) {
        throw new ConflictException("El precio base no puede ser menor al valor pretendido por el propietario ($" + producto.getMontoAsegurado() + ")");
    }

    producto.setEstado(Producto.EstadoProducto.ACEPTADO);
    productoRepository.save(producto);

    // El administrador coloca el precio mínimo de puja (precio base)
    itemCatalogoRepository.findByProductoId(id).stream().findFirst().ifPresent(item -> {
        item.setPrecioBase(precioBase);
        item.setSubastado(ItemCatalogo.subastado_bool.no);
        itemCatalogoRepository.save(item);
    });
}
public void eliminar(Long productoId, String documento) {
    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    if (!producto.getPropietarioUsuario().getDocumento().equals(documento)) {
        throw new com.subastaapp.exception.UnauthorizedException("No tenés permiso para eliminar este producto");
    }
    if (producto.getEstado() != Producto.EstadoProducto.PENDIENTE) {
        throw new com.subastaapp.exception.ConflictException("Solo se pueden eliminar productos en estado PENDIENTE");
    }
    productoRepository.delete(producto);
}

public List<ProductoResponse> listarPorUsuario(Long usuarioId) {
    if (!usuarioRepository.existsById(usuarioId)) {
        throw new ResourceNotFoundException("Usuario no encontrado");
    }
    return productoRepository.findByPropietarioUsuarioId(usuarioId)
            .stream().map(ProductoResponse::from).toList();
}

private LocalDate parseFecha(String fecha) {
    if (fecha == null || fecha.isBlank()) return null;
    String normalizada = fecha.replace("/", "-");
    for (DateTimeFormatter fmt : new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")}) {
        try { return LocalDate.parse(normalizada, fmt); } catch (DateTimeParseException ignored) {}
    }
    return null;
}

private Producto buildProducto(ProductoRequest req, Usuario propietario) {
    Producto p = new Producto();
    p.setFecha(parseFecha(req.getFecha()));
    p.setEstado(Producto.EstadoProducto.PENDIENTE);
    p.setDescripcionCompleta(req.getDescripcionCompleta());
    p.setPropietarioUsuario(propietario);
    p.setPolizaSeguro(req.getPolizaSeguro());
    p.setAseguradora(req.getAseguradora());
    p.setMontoAsegurado(req.getMontoAsegurado());
    p.setMonedaAsegurado(req.getMonedaAsegurado());
    p.setArtista(req.getArtista());
    p.setDisenador(req.getDisenador());
    p.setHistoria(req.getHistoria());
    p.setUbicacionDeposito(req.getUbicacionDeposito());
    p.setOrigenLicitoDeclarado(req.getOrigenLicitoDeclarado() != null && req.getOrigenLicitoDeclarado());
    p.setPropietarioDeclarado(req.getPropietarioDeclarado() != null && req.getPropietarioDeclarado());
    return p;
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
