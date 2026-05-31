package com.subastaapp.controller;

import com.subastaapp.businesslogic.MedioPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/medios-pago")
@RequiredArgsConstructor
public class MedioPagoController {

    private final MedioPagoService medioPagoService;

    @PatchMapping("/{id}/dar-de-alta")
    public ResponseEntity<Map<String, String>> verificarMedioPago(@PathVariable long id) {
        medioPagoService.verificarMedioPago(id);
        return ResponseEntity.ok(Map.of("mensaje", "Medio de pago verificado exitósamente."));
    }

    @PatchMapping("/{id}/dar-de-baja")
    public ResponseEntity<Map<String, String>> darDeBajaMedioPago(@PathVariable long id) {
        medioPagoService.darDeBajaMedioPago(id);
        return ResponseEntity.ok(Map.of("mensaje", "Medio de pago dado de baja exitosamente."));
    }
}