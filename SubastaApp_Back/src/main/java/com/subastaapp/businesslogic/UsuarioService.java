package com.subastaapp.businesslogic;

import com.subastaapp.config.JwtUtil;
import com.subastaapp.dto.request.*;
import com.subastaapp.dto.response.TokenResponse;
import com.subastaapp.dto.response.UsuarioResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.exception.UnauthorizedException;
import com.subastaapp.mapper.MedioPagoMapper;
import com.subastaapp.model.*;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedioPagoMapper medioPagoMapper;
    private final JwtUtil jwtUtil;

    public UsuarioResponse registrar(UsuarioRegistroRequest req) {
        if (usuarioRepository.existsByDocumento(req.getDocumento())) {
            throw new ConflictException("Ya existe un usuario con ese documento");
        }
        Usuario usuario = new Usuario();
        usuario.setDocumento(req.getDocumento());
        usuario.setNombre(req.getNombre());
        usuario.setDomicilio(req.getDomicilio());
        usuario.setApellido(req.getApellido());
        usuario.setPais(req.getPais());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setFotoDocumentoFrente(req.getFotoDocumentoFrente());
        usuario.setFotoDocumentoDorso(req.getFotoDocumentoDorso());
        if(req.getFotoPerfil() != null && !req.getFotoPerfil().isBlank()) {
            usuario.setFotoBase64(req.getFotoPerfil());
        }
        else{
            String defaultPfp = "https://ui-avatars.com/api/?name=" + req.getNombre() + "+" + req.getApellido() + "&background=random";
            usuario.setFotoBase64(defaultPfp);
        }

        //Mappear los medios de pago dto a entidades
        List<MedioPago> entidadesPago = req.getMedioPagos().stream()
                .map(pagoReq -> medioPagoMapper.toEntity(pagoReq, usuario))
                .collect(Collectors.toList());

        usuario.setMedioPagos(entidadesPago);
        usuario.setVerificado(Usuario.EstadoVerificacion.no);

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public TokenResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByDocumento(req.getDocumento())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        return new TokenResponse(jwtUtil.generateToken(usuario.getDocumento()));
    }

    public UsuarioResponse obtenerPorDocumento(String documento) {
        return UsuarioResponse.from(usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return UsuarioResponse.from(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    public UsuarioResponse actualizar(String documento, UsuarioUpdateRequest req) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (req.getNombre() != null) usuario.setNombre(req.getNombre());
        if (req.getDireccion() != null) usuario.setDomicilio(req.getDireccion());
        if (req.getFotoBase64() != null) usuario.setFotoBase64(req.getFotoBase64());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public void verificar(Long id, UsuarioVerificacionRequest req) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setVerificado(req.getVerificado());
        if (req.getVerificacionFinanciera() != null) usuario.setVerificacionFinanciera(req.getVerificacionFinanciera());
        if (req.getVerificacionJudicial() != null) usuario.setVerificacionJudicial(req.getVerificacionJudicial());
        if (req.getCalificacionRiesgo() != null) {
            usuario.setCalificacionRiesgo(req.getCalificacionRiesgo());
            usuario.setCategoria(calcularCategoria(req.getCalificacionRiesgo()));
        }
        usuarioRepository.save(usuario);
    }

    private Usuario.CategoriaUsuario calcularCategoria(int calificacion) {
        return switch (calificacion) {
            case 1, 2 -> Usuario.CategoriaUsuario.comun;
            case 3 -> Usuario.CategoriaUsuario.especial;
            case 4 -> Usuario.CategoriaUsuario.plata;
            case 5 -> Usuario.CategoriaUsuario.oro;
            case 6 -> Usuario.CategoriaUsuario.platino;
            default -> Usuario.CategoriaUsuario.comun;
        };
    }
}
