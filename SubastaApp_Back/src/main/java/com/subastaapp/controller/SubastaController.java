package com.subastaapp.controller;

import com.subastaapp.businesslogic.AsistenteService;
import com.subastaapp.businesslogic.SubastaService;
import com.subastaapp.dto.request.AsistenteRequest;
import com.subastaapp.dto.request.SubastaRequest;
import com.subastaapp.dto.response.AsistenteResponse;
import com.subastaapp.dto.response.RegistroSubastaResponse;
import com.subastaapp.dto.response.SubastaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subastas")
@RequiredArgsConstructor
public class SubastaController {

    private final SubastaService subastaService;
    private final AsistenteService asistenteService;

    @PostMapping
    public ResponseEntity<SubastaResponse> crear(Authentication auth, @Valid @RequestBody SubastaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subastaService.crear(req, auth.getName()));
    }

    @GetMapping("/abiertas")
    public ResponseEntity<List<SubastaResponse>> listarAbiertas() {
        return ResponseEntity.ok(subastaService.listarAbiertas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubastaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(subastaService.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        subastaService.cambiarEstado(id, body.get("estado"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/asistentes")
    public ResponseEntity<AsistenteResponse> registrarAsistente(@PathVariable Long id,
                                                                  @Valid @RequestBody AsistenteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenteService.registrar(id, req));
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<Void> cerrar(@PathVariable Long id) {
        subastaService.cerrar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/registro")
    public ResponseEntity<List<RegistroSubastaResponse>> obtenerRegistro(@PathVariable Long id) {
        return ResponseEntity.ok(subastaService.obtenerRegistro(id));
    }
}
