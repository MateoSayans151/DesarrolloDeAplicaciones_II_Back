package com.subastaapp.controller;

import com.subastaapp.dto.request.ProductoRequest;
import com.subastaapp.dto.response.ProductoResponse;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.dto.request.FotoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/catalogo/{id}")
    public ResponseEntity<ProductoResponse> crearEnCatalogo(Authentication auth,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearEnCatalogo(id, req, auth.getName()));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Void> aprobar(@PathVariable Long id) {
        productoService.aprobarProducto(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> agregarFoto(@PathVariable Long id,
                                             @Valid @RequestBody FotoRequest req) {
        productoService.agregarFoto(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
