package com.subastaapp.controller;

import com.subastaapp.businesslogic.NotificacionService;
import com.subastaapp.dto.request.NotificacionRequest;
import com.subastaapp.dto.response.NotificacionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping("/admin/categoria/{categoria}")
    public ResponseEntity<NotificacionResponse> enviarACategoria(@PathVariable String categoria,
                                                                  @Valid @RequestBody NotificacionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.enviarACategoria(categoria, req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificacionResponse>> listarMisNotificaciones(Authentication auth) {
        return ResponseEntity.ok(notificacionService.listarParaUsuario(auth.getName()));
    }
}
