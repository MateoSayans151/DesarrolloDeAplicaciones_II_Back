package com.subastaapp.controller;

import com.subastaapp.dto.request.AprobacionProductoRequest;
import com.subastaapp.dto.request.ProductoRequest;
import com.subastaapp.dto.request.RechazoProductoRequest;
import com.subastaapp.dto.response.ProductoResponse;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.dto.request.FotoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(Authentication auth,
                                                  @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(req, auth.getName()));
    }

    @PostMapping("/catalogo/{id}")
    public ResponseEntity<ProductoResponse> crearEnCatalogo(Authentication auth,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearEnCatalogo(id, req, auth.getName()));
    }

    @PatchMapping("/admin/{id}/aprobar")
    public ResponseEntity<Void> aprobar(@PathVariable Long id,
                                        @Valid @RequestBody AprobacionProductoRequest req) {
        productoService.aprobarProducto(id, req.getPrecioBase());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/{id}/rechazar")
    public ResponseEntity<Void> rechazar(@PathVariable Long id,
                                          @Valid @RequestBody RechazoProductoRequest req) {
        productoService.rechazarProducto(id, req.getMotivoRechazo());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/aceptar-precio")
    public ResponseEntity<Void> aceptarPrecio(@PathVariable Long id, Authentication auth) {
        productoService.aceptarPrecio(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/rechazar-precio")
    public ResponseEntity<Void> rechazarPrecio(@PathVariable Long id, Authentication auth) {
        productoService.rechazarPrecio(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        productoService.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> agregarFoto(@PathVariable Long id,
                                             @Valid @RequestBody FotoRequest req) {
        productoService.agregarFoto(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
