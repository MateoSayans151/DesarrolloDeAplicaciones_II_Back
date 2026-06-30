package com.subastaapp.controller;

import com.subastaapp.businesslogic.AsistenteService;
import com.subastaapp.businesslogic.ProductoService;
import com.subastaapp.businesslogic.PujaService;
import com.subastaapp.businesslogic.SubastaService;
import com.subastaapp.businesslogic.UsuarioService;
import com.subastaapp.dto.request.*;
import com.subastaapp.dto.response.EstadisticasUsuarioResponse;
import com.subastaapp.dto.response.MiPujaResponse;
import com.subastaapp.dto.response.MedioPagoResponse;
import com.subastaapp.dto.response.NivelProgressResponse;
import com.subastaapp.dto.response.ProductoResponse;
import com.subastaapp.dto.response.SubastaResponse;
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
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SubastaService subastaService;
    private final ProductoService productoService;
    private final AsistenteService asistenteService;
    private final PujaService pujaService;

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

    @GetMapping("/admin/pendientes")
    public ResponseEntity<List<UsuarioResponse>> listarPendientes() {
        return ResponseEntity.ok(usuarioService.listarPendientes());
    }

    @PostMapping("/admin/{id}/verificacion")
    public ResponseEntity<Map<String, String>> verificar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioVerificacionRequest req) {
        String token = usuarioService.verificar(id, req);
        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.ok(Map.of());
    }
    @PatchMapping("/admin/{id}/medios-pago")
    public ResponseEntity<UsuarioResponse> verificarMediosPago(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.verificarMediosPago(id));
    }

    @GetMapping("/dev/token-registro/{documento}")
    public ResponseEntity<TokenResponse> obtenerTokenRegistroDev(@PathVariable String documento) {
        return ResponseEntity.ok(usuarioService.obtenerTokenRegistroDev(documento));
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

    @GetMapping("/{id}/subastas")
    public ResponseEntity<List<SubastaResponse>> obtenerSubastasDeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(subastaService.listarPorUsuario(id));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosDeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.listarPorUsuario(id));
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasUsuarioResponse> obtenerEstadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerEstadisticas(id));
    }

    @GetMapping("/{id}/asistencias")
    public ResponseEntity<List<SubastaResponse>> obtenerAsistencias(@PathVariable Long id) {
        return ResponseEntity.ok(asistenteService.listarSubastasPorUsuario(id));
    }

    @GetMapping("/{id}/pujas")
    public ResponseEntity<List<MiPujaResponse>> obtenerMisPujas(@PathVariable Long id) {
        return ResponseEntity.ok(pujaService.listarMisPujas(id));
    }

    @GetMapping("/{id}/nivel")
    public ResponseEntity<NivelProgressResponse> obtenerNivelProgress(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerNivelProgress(id));
    }
}
