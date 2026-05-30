package com.subastaapp.controller;

import com.subastaapp.dto.request.ProductoRequest;
import com.subastaapp.dto.response.ProductoResponse;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.dto.request.FotoRequest;
import com.subastaapp.dto.response.ProductoResponse;
import jakarta.validation.Valid;
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

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> agregarFoto(@PathVariable Long id,
                                             @Valid @RequestBody FotoRequest req) {
        productoService.agregarFoto(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
