package com.subastaapp.businesslogic;

import com.subastaapp.config.JwtUtil;
import com.subastaapp.dto.request.*;
import com.subastaapp.dto.response.EstadisticasUsuarioResponse;
import com.subastaapp.dto.response.MedioPagoResponse;
import com.subastaapp.dto.response.NivelProgressResponse;
import com.subastaapp.dto.response.TokenResponse;
import com.subastaapp.dto.response.UsuarioPublicoResponse;
import com.subastaapp.dto.response.UsuarioResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.exception.UnauthorizedException;
import com.subastaapp.mapper.MedioPagoMapper;
import com.subastaapp.model.*;
import com.subastaapp.repository.MedioPagoRepository;
import com.subastaapp.repository.NivelCategoriaRepository;
import com.subastaapp.repository.ProductoRepository;
import com.subastaapp.repository.PujaRepository;
import com.subastaapp.repository.RegistroSubastaRepository;
import com.subastaapp.repository.SubastaRepository;
import com.subastaapp.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NivelCategoriaRepository nivelCategoriaRepository;
    private final PujaRepository pujaRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final SubastaRepository subastaRepository;
    private final ProductoRepository productoRepository;
    private final RegistroSubastaRepository registroSubastaRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedioPagoMapper medioPagoMapper;
    private final JwtUtil jwtUtil;

    public UsuarioResponse registroInicial(UsuarioRegistroInicialRequest r){
        if (usuarioRepository.existsByDocumento(r.getDocumento())) {
            throw new ConflictException("Ya existe un usuario con ese documento");
        }
        Usuario usuario = new Usuario();
        usuario.setDocumento(r.getDocumento());
        usuario.setNombre(r.getNombre());
        usuario.setApellido(r.getApellido());
        usuario.setPais(r.getPais());
        usuario.setDomicilio(r.getLocalidad()+", "+r.getCalle()+", "+r.getNumeroCalle()+", "+r.getCodigoPostal());
        usuario.setFotoDocumentoFrente(r.getFotoDocumentoFrente());
        usuario.setFotoDocumentoDorso(r.getFotoDocumentoDorso());
        String defaultPfp = "https://ui-avatars.com/api/?name=" + r.getNombre() + "+" + r.getApellido() + "&background=random";
        usuario.setFotoBase64(defaultPfp);
        //contraseña temporal hasta que la cambie en el registro final
        usuario.setPassword(UUID.randomUUID().toString());
        usuario.setVerificado(Usuario.EstadoVerificacion.no);

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public UsuarioResponse registroFinal(UsuarioRegistroFinalRequest r, String documento){
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if(usuario.getVerificado() == Usuario.EstadoVerificacion.no){
            throw new UnauthorizedException("Aún no fuiste verificado");
        }
        if (usuario.getMedioPagos() != null && !usuario.getMedioPagos().isEmpty()) {
            throw new ConflictException("Este usuario ya completó su registro final.");
        }
        usuario.setPassword(passwordEncoder.encode(r.getPassword()));
        //Mappear los medios de pago dto a entidades
        List<MedioPago> entidadesPago = r.getMedioPagos().stream()
                .map(pagoReq -> medioPagoMapper.toEntity(pagoReq, usuario))
                .collect(Collectors.toList());
        usuario.setMedioPagos(entidadesPago);

        return  UsuarioResponse.from(usuarioRepository.save(usuario));
    }


    public TokenResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByDocumento(req.getDocumento())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        if(usuario.getVerificado() == Usuario.EstadoVerificacion.no){
            throw new UnauthorizedException("Tu usuario aún no fue verificado");
        }
        return new TokenResponse(jwtUtil.generateToken(usuario.getDocumento()));
    }

    public UsuarioResponse obtenerPorDocumento(String documento) {
        return UsuarioResponse.from(usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    public UsuarioPublicoResponse obtenerPorId(Long id) {
        return UsuarioPublicoResponse.from(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    public UsuarioResponse actualizar(String documento, UsuarioUpdateRequest req) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (req.getNombre() != null) usuario.setNombre(req.getNombre());
        if (req.getDomicilio() != null) usuario.setDomicilio(req.getDomicilio());
        if (req.getFotoBase64() != null) usuario.setFotoBase64(req.getFotoBase64());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public List<UsuarioResponse> listarPendientes() {
        return usuarioRepository.findByVerificado(Usuario.EstadoVerificacion.no)
                .stream().map(UsuarioResponse::from).toList();
    }

    public String verificar(Long id, UsuarioVerificacionRequest req) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setVerificado(req.getVerificado());
        if (req.getVerificacionFinanciera() != null) usuario.setVerificacionFinanciera(req.getVerificacionFinanciera());
        if (req.getVerificacionJudicial() != null) usuario.setVerificacionJudicial(req.getVerificacionJudicial());
        if (req.getCalificacionRiesgo() != null) {
            usuario.setCalificacionRiesgo(req.getCalificacionRiesgo());
        }

        NivelCategoria nivelComun = nivelCategoriaRepository.findByNombre("comun")
        .orElseThrow(() -> new ResourceNotFoundException("Nivel 'comun' no encontrado"));
        usuario.setNivelCategoria(nivelComun);

        usuarioRepository.save(usuario);

        if (req.getVerificado() == Usuario.EstadoVerificacion.si) {
            return jwtUtil.generateToken(usuario.getDocumento());
        }
        return null;
    }

    public TokenResponse obtenerTokenRegistroDev(String documento){
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));

        if(usuario.getVerificado() != Usuario.EstadoVerificacion.si){
            throw new UnauthorizedException("El usuario aún no fue verificado por un administrador");
        }

        return new TokenResponse(jwtUtil.generateToken(usuario.getDocumento()));
    }

    public MedioPagoResponse getMedioPagoById(Long id, String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        MedioPago mp = medioPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un medio de pago con ese ID"));
        if (! mp.getUsuario().getId().equals(usuario.getId())){
            throw new UnauthorizedException("Este medio de pago le pertenece a otra persona");
        }

        return MedioPagoResponse.from(mp);
    }

    public List<MedioPagoResponse> listarMediosPago(String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<MedioPago> medios = usuario.getMedioPagos();
        List<MedioPagoResponse> mediosPagoResponse = new ArrayList<>();
        for (MedioPago medioPago : medios) {
            mediosPagoResponse.add(MedioPagoResponse.from(medioPago));
        }
        return mediosPagoResponse;
    }

    public MedioPagoResponse agregarMedioPago(MedioPagoRequest req, String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        MedioPago mp = medioPagoMapper.toEntity(req, usuario);
        mp = medioPagoRepository.save(mp);
        return MedioPagoResponse.from(mp);
    }

    public EstadisticasUsuarioResponse obtenerEstadisticas(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        EstadisticasUsuarioResponse stats = new EstadisticasUsuarioResponse();
        stats.setUsuarioId(id);
        stats.setCategoria(usuario.getNivelCategoria() != null ? usuario.getNivelCategoria().getNombre() : null);
        stats.setSubastasCreadas(subastaRepository.countByCreadorUsuarioId(id));
        stats.setProductosSubidos(productoRepository.countByPropietarioUsuarioId(id));
        stats.setProductosVendidos(registroSubastaRepository.countByPropietarioUsuarioId(id));
        stats.setPujasGanadas(registroSubastaRepository.countByCompradorUsuarioId(id));
        stats.setMontoTotalGastado(registroSubastaRepository.sumImporteByCompradorUsuarioId(id));
        return stats;
    }

    @Transactional
    public void eliminarMedioPago(Long idMedioPago, String documento) {
        MedioPago medioPago = medioPagoRepository.findById(idMedioPago)
                .orElseThrow(() -> new ResourceNotFoundException("Medio de pago no encontrado"));

        if (!medioPago.getUsuario().getDocumento().equals(documento)) {
            throw new UnauthorizedException("No tenes permiso para eliminar este medio de pago.");
        }
        medioPagoRepository.delete(medioPago);
    }

    public void actualizarNivelSiCorresponde(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        NivelProgressResponse nivelProgress = obtenerNivelProgress(usuarioId);
        String nivelCalculado = nivelProgress.getNivelActual();
        String nivelActualNombre = usuario.getNivelCategoria() != null
                ? usuario.getNivelCategoria().getNombre() : null;

        if (!nivelCalculado.equals(nivelActualNombre)) {
            NivelCategoria nuevoNivel = nivelCategoriaRepository.findByNombre(nivelCalculado)
                    .orElseThrow(() -> new ResourceNotFoundException("Nivel '" + nivelCalculado + "' no encontrado"));
            usuario.setNivelCategoria(nuevoNivel);
            usuarioRepository.save(usuario);
        }
    }

    public NivelProgressResponse obtenerNivelProgress(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        long pujasGanadas = pujaRepository.countByAsistenteUsuarioIdAndGanador(
                usuarioId, Puja.EstadoGanador.si);

        List<NivelCategoria> niveles = nivelCategoriaRepository.findAllByOrderByPujasGanadasNecesariasAsc();

        NivelCategoria nivelActual;
        int indexActual = 0;

        if (usuario.getNivelCategoria() != null) {
            // El usuario ya tiene un nivel asignado (ej: id=4, "Platino") -> se respeta ese
            nivelActual = usuario.getNivelCategoria();
            for (int i = 0; i < niveles.size(); i++) {
                if (niveles.get(i).getId().equals(nivelActual.getId())) {
                    indexActual = i;
                    break;
                }
            }
        } else {
            // Sin nivel asignado -> se calcula a partir de las pujas ganadas
            nivelActual = niveles.get(0);
            for (int i = 0; i < niveles.size(); i++) {
                if (pujasGanadas >= niveles.get(i).getPujasGanadasNecesarias()) {
                    nivelActual = niveles.get(i);
                    indexActual = i;
                }
            }
        }

        NivelCategoria nivelSiguiente = indexActual < niveles.size() - 1
                ? niveles.get(indexActual + 1) : null;

        double progreso;
        Integer pujasParaSiguiente = null;

        if (nivelSiguiente == null) {
            progreso = 1.0;
        } else {
            pujasParaSiguiente = nivelSiguiente.getPujasGanadasNecesarias();
            int base = nivelActual.getPujasGanadasNecesarias();
            int rango = pujasParaSiguiente - base;
            progreso = rango > 0 ? (double) (pujasGanadas - base) / rango : 1.0;
        }

        return new NivelProgressResponse(
                nivelActual.getNombre(),
                (int) pujasGanadas,
                pujasParaSiguiente,
                progreso,
                nivelActual.getNombre()
        );


    }
    public UsuarioResponse verificarMediosPago(Long id) {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            if (usuario.getMedioPagos() == null || usuario.getMedioPagos().isEmpty()) {
                throw new ConflictException("El usuario no tiene medios de pago registrados");
            }
            usuario.getMedioPagos().forEach(medioPago -> {
                if (medioPago.getVerificado() != MedioPago.EstadoVerificacion.si) {
                    medioPago.setVerificado(MedioPago.EstadoVerificacion.si);
                }
            });
            return new UsuarioResponse(); // Replace with actual response
        }
}