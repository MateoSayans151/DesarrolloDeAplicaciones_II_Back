package com.subastaapp.controller;

import com.subastaapp.businesslogic.UsuarioService;
import com.subastaapp.dto.request.LoginRequest;
import com.subastaapp.dto.request.UsuarioRegistroRequest;
import com.subastaapp.dto.request.UsuarioUpdateRequest;
import com.subastaapp.dto.request.UsuarioVerificacionRequest;
import com.subastaapp.dto.response.TokenResponse;
import com.subastaapp.dto.response.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(req));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(usuarioService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.obtenerPorDocumento(auth.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(Authentication auth,
                                                             @Valid @RequestBody UsuarioUpdateRequest req) {
        return ResponseEntity.ok(usuarioService.actualizar(auth.getName(), req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping("/{id}/verificacion")
    public ResponseEntity<Void> verificar(@PathVariable Long id,
                                          @Valid @RequestBody UsuarioVerificacionRequest req) {
        usuarioService.verificar(id, req);
        return ResponseEntity.ok().build();
    }
}
