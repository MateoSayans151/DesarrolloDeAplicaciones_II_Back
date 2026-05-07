package com.subastaapp.controller;

import com.subastaapp.businesslogic.CatalogoService;
import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.dto.request.CatalogoRequest;
import com.subastaapp.dto.request.ItemCatalogoRequest;
import com.subastaapp.dto.request.ProductoRequest;
import com.subastaapp.dto.response.CatalogoDetalleResponse;
import com.subastaapp.dto.response.CatalogoResponse;
import com.subastaapp.dto.response.ItemCatalogoResponse;
import com.subastaapp.dto.response.ProductoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<CatalogoResponse> crear(@Valid @RequestBody CatalogoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(req));
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<CatalogoDetalleResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.obtenerDetalle(id));
    }

    @GetMapping("/publico/{subastaId}")
    public ResponseEntity<List<ItemCatalogoResponse>> obtenerPublico(@PathVariable Long subastaId) {
        return ResponseEntity.ok(catalogoService.obtenerCatalogoPublico(subastaId));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ItemCatalogoResponse> agregarItem(@PathVariable Long id,
                                                             @Valid @RequestBody ItemCatalogoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.agregarItem(id, req));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoResponse> crearProducto(@PathVariable Long id,
                                                           @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearEnCatalogo(id, req));
    }
}
