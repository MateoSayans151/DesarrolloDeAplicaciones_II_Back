package com.subastaapp.businesslogic;

import com.subastaapp.dto.request.SubastaRequest;
import com.subastaapp.dto.response.RegistroSubastaResponse;
import com.subastaapp.dto.response.SubastaResponse;
import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.*;
import com.subastaapp.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubastaService {

    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PujaRepository pujaRepository;
    private final RegistroSubastaRepository registroSubastaRepository;
    private final CatalogoRepository catalogoRepository;
    private final AsistenteRepository asistenteRepository;
    private final UsuarioService usuarioService;

    private final TransactionTemplate transactionTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Value("${subasta.tiempo.item.segundos:60}")
    private long tiempoItemSegundos;

    public SubastaResponse crear(SubastaRequest req, String documento) {
        Usuario creador = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Subasta subasta = new Subasta();
        subasta.setFecha(req.getFecha());
        subasta.setHora(req.getHora());
        subasta.setEstado(Subasta.EstadoSubasta.programada);
        subasta.setTiempoItemSegundos(req.getTiempoItemSegundos());
        subasta.setCreadorUsuario(creador);
        subasta.setUbicacion(req.getUbicacion());
        subasta.setCapacidadAsistentes(req.getCapacidadAsistentes());
        subasta.setTieneDeposito(req.getTieneDeposito());
        subasta.setSeguridadPropia(req.getSeguridadPropia());
        subasta.setCategoria(req.getCategoria());
        return SubastaResponse.from(subastaRepository.save(subasta));
    }

    public List<SubastaResponse> listarTodas() {
        return subastaRepository.findAll()
                .stream().map(SubastaResponse::from).toList();
    }

    public List<SubastaResponse> listarAbiertas() {
        return subastaRepository.findByEstado(Subasta.EstadoSubasta.abierta)
                .stream().map(SubastaResponse::from).toList();
    }

    public SubastaResponse obtener(Long id) {
        return SubastaResponse.from(subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada")));
    }


    public void cambiarEstado(Long id, String estado) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        try {
            Subasta.EstadoSubasta nuevoEstado = Subasta.EstadoSubasta.valueOf(estado);

            System.out.println("==========================================================");
            System.out.println("SUBASTA " + id + ": Solicitud de cambio de estado a -> " + nuevoEstado.name().toUpperCase());

            if(nuevoEstado == Subasta.EstadoSubasta.abierta && subasta.getEstado() != Subasta.EstadoSubasta.abierta) {
                subasta.setEstado(nuevoEstado);
                subastaRepository.save(subasta);
                System.out.println("SUBASTA " + id + ": Estado actualizado exitosamente. Disparando secuencia de remate...");
                iniciarSubastaSecuencial(subasta.getId());
                return;
            }

            subasta.setEstado(nuevoEstado);
            subastaRepository.save(subasta);
            System.out.println("SUBASTA " + id + ": Estado modificado sin disparar secuencia.");
            System.out.println("==========================================================");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido: " + estado);
        }
    }

    private void iniciarSubastaSecuencial(Long subastaId) {
        Subasta s = subastaRepository.findById(subastaId).orElse(null);
        long tiempoEfectivo = (s != null && s.getTiempoItemSegundos() != null)
                ? s.getTiempoItemSegundos()
                : tiempoItemSegundos;

        catalogoRepository.findBySubastaId(subastaId).ifPresent(catalogo -> {
            System.out.println("SUBASTA " + subastaId + ": Escaneando catálogo (ID: " + catalogo.getId() + ") en búsqueda de ítems pendientes...");

            // Extraemos SOLO LOS IDs para evitar que las entidades caduquen en la memoria del temporizador
            List<Long> itemsPendientesIds = itemCatalogoRepository.findByCatalogoId(catalogo.getId())
                    .stream()
                    .filter(item -> item.getSubastado() == ItemCatalogo.subastado_bool.no)
                    .map(item -> (long) item.getId())
                    .toList();

            if(!itemsPendientesIds.isEmpty()){
                System.out.println("SUBASTA " + subastaId + ": Se encontraron " + itemsPendientesIds.size() + " ítems listos para ser rematados.");
                avanzarAlSiguienteItem(subastaId, itemsPendientesIds, 0, tiempoEfectivo);
            }
            else{
                System.out.println("SUBASTA " + subastaId + ": ERROR - Intento de apertura rechazado. Todos los ítems ya fueron subastados.");
                cambiarEstado(subastaId, "cerrada");
            }
        });
    }

    private void avanzarAlSiguienteItem(Long subastaId, List<Long> itemIds, int indexActual, long tiempoEfectivo) {
        transactionTemplate.execute(status -> {
            try {
                Subasta subasta = subastaRepository.findById(subastaId).orElse(null);
                if (subasta == null || subasta.getEstado() != Subasta.EstadoSubasta.abierta) {
                    System.out.println("SUBASTA " + subastaId + ": Ejecución interrumpida. La subasta ya no se encuentra abierta.");
                    return null;
                }

                // 1. Cierre del ítem anterior
                if (indexActual > 0) {
                    Long itemAnteriorId = itemIds.get(indexActual - 1);
                    // Volvemos a buscar el item a la base de datos para tenerlo vinculado
                    ItemCatalogo itemAnterior = itemCatalogoRepository.findById(itemAnteriorId).orElse(null);

                    if (itemAnterior != null) {
                        System.out.println("SUBASTA " + subastaId + ": ¡Tiempo agotado para el ítem " + itemAnterior.getId() + "!");
                        cerrarItemIndividual(subasta, itemAnterior);
                    }
                }

                // 2. Apertura del nuevo ítem
                if (indexActual < itemIds.size()) {
                    Long nuevoItemId = itemIds.get(indexActual);
                    // Buscamos el ítem fresco para poder acceder a .getProducto() sin fallos de LazyLoading
                    ItemCatalogo nuevoItem = itemCatalogoRepository.findById(nuevoItemId).orElse(null);

                    if (nuevoItem != null) {
                        subasta.setItemActivoId(Math.toIntExact(nuevoItem.getId()));
                        subasta.setItemActivoDesde(System.currentTimeMillis());
                        subastaRepository.save(subasta);

                        System.out.println("\n----------------------------------------------------------");
                        System.out.println("SUBASTA " + subastaId + ": >> INICIA EL REMATE DEL ÍTEM " + nuevoItem.getId() + " <<");
                        System.out.println("SUBASTA " + subastaId + ": Producto: " + nuevoItem.getProducto().getDescripcionCompleta());
                        System.out.println("SUBASTA " + subastaId + ": Precio Base: $" + nuevoItem.getPrecioBase());
                        System.out.println("SUBASTA " + subastaId + ": Se reciben ofertas por " + tiempoEfectivo + " segundos...");
                        System.out.println("----------------------------------------------------------\n");

                        // Programar el siguiente salto
                        scheduler.schedule(() -> {
                            avanzarAlSiguienteItem(subastaId, itemIds, indexActual + 1, tiempoEfectivo);
                        }, tiempoEfectivo, TimeUnit.SECONDS);
                    }
                } else {
                    // 3. Ya no hay más items
                    subasta.setItemActivoId(null);
                    subasta.setItemActivoDesde(null);
                    subasta.setEstado(Subasta.EstadoSubasta.cerrada);
                    subastaRepository.save(subasta);
                    System.out.println("==========================================================");
                    System.out.println("SUBASTA " + subastaId + ": FINALIZADA EXITOSAMENTE. Todos los ítems fueron rematados.");
                    System.out.println("==========================================================");
                }
            } catch (Exception e) {
                // Si explota cualquier cosa, ahora sí nos enteramos en la consola
                System.err.println("==========================================================");
                System.err.println("ERROR CRITICO EN HILO ASINCRONO (SUBASTA " + subastaId + "): " + e.getMessage());
                e.printStackTrace();
                System.err.println("==========================================================");
            }
            return null;
        });
    }

    private void cerrarItemIndividual(Subasta subasta, ItemCatalogo item) {
        System.out.println("SUBASTA " + subasta.getId() + ": Procesando cierre del ítem " + item.getId() + "...");

        Optional<Puja> pujaGanadora = pujaRepository.findTopByItemIdOrderByImporteDesc(item.getId());

        RegistroSubasta registro = new RegistroSubasta();
        registro.setSubasta(subasta);
        registro.setPropietarioUsuario(item.getProducto().getPropietarioUsuario());
        registro.setProducto(item.getProducto());
        registro.setComision(item.getComision());

        if (pujaGanadora.isPresent()) {
            Puja puja = pujaGanadora.get();
            puja.setGanador(Puja.EstadoGanador.si);
            pujaRepository.save(puja);

            registro.setCompradorUsuario(puja.getAsistente().getUsuario());
            registro.setImporte(puja.getImporte());

            usuarioService.actualizarNivelSiCorresponde(puja.getAsistente().getUsuario().getId());

            System.out.println("SUBASTA " + subasta.getId() + ": ¡VENDIDO! Ganador: Usuario ID " + puja.getAsistente().getUsuario().getId() + " - Oferta final: $" + puja.getImporte());
        } else {
            registro.setCompradorUsuario(null); // null representará a la Empresa
            registro.setImporte(item.getPrecioBase());

            System.out.println("SUBASTA " + subasta.getId() + ": Sin ofertas. La EMPRESA adquiere el producto por el valor base de $" + item.getPrecioBase());
        }

        item.setSubastado(ItemCatalogo.subastado_bool.si);
        itemCatalogoRepository.save(item);
        registroSubastaRepository.save(registro);

        System.out.println("SUBASTA " + subasta.getId() + ": Ítem " + item.getId() + " guardado en el registro histórico.");
    }

    @Transactional
    public void cerrar(Long id) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        if (subasta.getEstado() == Subasta.EstadoSubasta.cerrada) {
            throw new ConflictException("La subasta ya se encuentra cerrada");
        }

        // Buscar el catalogo de la subasta
        catalogoRepository.findBySubastaId(id).ifPresent(catalogo -> {
            List<ItemCatalogo> items = itemCatalogoRepository.findByCatalogoId(catalogo.getId());
            for (ItemCatalogo item : items) {
                pujaRepository.findTopByItemIdOrderByImporteDesc(item.getId()).ifPresent(puja -> {
                    // Marcar puja ganadora
                    puja.setGanador(Puja.EstadoGanador.si);
                    pujaRepository.save(puja);

                    // Registrar resultado
                    RegistroSubasta registro = new RegistroSubasta();
                    registro.setSubasta(subasta);
                    registro.setPropietarioUsuario(item.getProducto().getPropietarioUsuario());
                    registro.setProducto(item.getProducto());
                    registro.setCompradorUsuario(puja.getAsistente().getUsuario());
                    registro.setImporte(puja.getImporte());
                    registro.setComision(item.getComision());
                    registroSubastaRepository.save(registro);
                });
            }
        });

        subasta.setEstado(Subasta.EstadoSubasta.cerrada);
        subastaRepository.save(subasta);
    }

    public List<RegistroSubastaResponse> obtenerRegistro(Long id) {
        if (!subastaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subasta no encontrada");
        }
        return registroSubastaRepository.findBySubastaId(id)
                .stream().map(RegistroSubastaResponse::from).toList();
    }

    @Transactional
    public void eliminar(Long id) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subasta no encontrada"));
        catalogoRepository.findBySubastaId(id).ifPresent(catalogo -> {
            List<ItemCatalogo> items = itemCatalogoRepository.findByCatalogoId(catalogo.getId());
            for (ItemCatalogo item : items) {
                pujaRepository.deleteByItemId(item.getId());
            }
            itemCatalogoRepository.deleteAll(items);
            catalogoRepository.delete(catalogo);
        });
        asistenteRepository.deleteBySubastaId(id);
        registroSubastaRepository.deleteBySubastaId(id);
        subastaRepository.delete(subasta);
    }

    public List<SubastaResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return subastaRepository.findByCreadorUsuarioId(usuarioId)
                .stream().map(SubastaResponse::from).toList();
    }
}