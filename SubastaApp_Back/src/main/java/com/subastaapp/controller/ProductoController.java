package com.subastaapp.controller;

import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.dto.request.FotoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> agregarFoto(@PathVariable Long id,
                                             @Valid @RequestBody FotoRequest req) {
        productoService.agregarFoto(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
