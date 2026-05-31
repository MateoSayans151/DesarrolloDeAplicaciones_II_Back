package com.subastaapp.controller;

import com.subastaapp.businesslogic.UsuarioService;
import com.subastaapp.dto.request.*;
import com.subastaapp.dto.response.MedioPagoResponse;
import com.subastaapp.dto.response.TokenResponse;
import com.subastaapp.dto.response.UsuarioPublicoResponse;
import com.subastaapp.dto.response.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro-inicial")
    public ResponseEntity<UsuarioResponse> registroInicial(@Valid @RequestBody UsuarioRegistroInicialRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registroInicial(req));
    }

    @PostMapping("/registro-final")
    public ResponseEntity<UsuarioResponse> registroFinal(@Valid @RequestBody UsuarioRegistroFinalRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registroFinal(req, auth.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(usuarioService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.obtenerPorDocumento(auth.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(Authentication auth,
                                                             @Valid @RequestBody UsuarioUpdateRequest req) {
        return ResponseEntity.ok(usuarioService.actualizar(auth.getName(), req));
    }

    @GetMapping("/{id}/perfil-publico")
    public ResponseEntity<UsuarioPublicoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping("/{id}/verificacion")
    public ResponseEntity<Void> verificar(@PathVariable Long id,
                                          @Valid @RequestBody UsuarioVerificacionRequest req) {
        usuarioService.verificar(id, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/medios-pago")
    public ResponseEntity<List<MedioPagoResponse>> obtenerMediosPago(Authentication auth) {
        return ResponseEntity.ok(usuarioService.listarMediosPago(auth.getName()));
    }

    @GetMapping("/me/medios-pago/{id}")
    public ResponseEntity<MedioPagoResponse> obtenerMedioPago(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(usuarioService.getMedioPagoById(id, auth.getName()));
    }

    @PostMapping("/me/medios-pago")
    public ResponseEntity<MedioPagoResponse> agregarMedioPago(@Valid @RequestBody MedioPagoRequest req, Authentication auth) {
        return ResponseEntity.ok(usuarioService.agregarMedioPago(req, auth.getName()));
    }

    @DeleteMapping("/me/medios-pago/{id_medioPago}")
    public ResponseEntity<MedioPagoResponse> eliminarMedioPago(@PathVariable Long id_medioPago, Authentication auth) {
        usuarioService.eliminarMedioPago(id_medioPago, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
