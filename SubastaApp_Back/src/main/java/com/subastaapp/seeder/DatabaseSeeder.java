package com.subastaapp.seeder;

import com.subastaapp.model.*;
import com.subastaapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final SubastaRepository subastaRepository;
    private final CatalogoRepository catalogoRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final AsistenteRepository asistenteRepository;
    private final PujaRepository pujaRepository;
    private final RegistroSubastaRepository registroSubastaRepository;
    private final NivelCategoriaRepository nivelCategoriaRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (productoRepository.count() > 0 || subastaRepository.count() > 0 || asistenteRepository.count() > 0) {
            System.out.println("La base de datos ya contiene registros. Se omite el Seeder.");
            return;
        }

        System.out.println("Iniciando carga de datos de prueba...");

        // 1. CREAR NIVELES DE CATEGORÍA
        nivelCategoriaRepository.findByNombre("comun")
                .orElseGet(() -> nivelCategoriaRepository.save(new NivelCategoria(null, "comun", 0)));
        nivelCategoriaRepository.findByNombre("plata")
                .orElseGet(() -> nivelCategoriaRepository.save(new NivelCategoria(null, "plata", 3)));
        nivelCategoriaRepository.findByNombre("oro")
                .orElseGet(() -> nivelCategoriaRepository.save(new NivelCategoria(null, "oro", 5)));
        NivelCategoria nivelPlatino = nivelCategoriaRepository.findByNombre("platino")
                .orElseGet(() -> nivelCategoriaRepository.save(new NivelCategoria(null, "platino", 10)));
        nivelCategoriaRepository.findByNombre("especial")
                .orElseGet(() -> nivelCategoriaRepository.save(new NivelCategoria(null, "especial", 15)));

        // 3. OBTENER USUARIOS EXISTENTES
        List<Usuario> admins = usuarioRepository.findAll().stream()
                .filter(u -> u.getRole() == Usuario.User_roles.ADMIN)
                .toList();
        List<Usuario> users = usuarioRepository.findAll().stream()
                .filter(u -> u.getRole() == Usuario.User_roles.USER)
                .toList();

        if (admins.isEmpty() || users.isEmpty()) {
            System.out.println("ADVERTENCIA: No hay suficientes usuarios en la tabla para poblar la DB. Registra un admin y un usuario primero.");
            return;
        }

        Usuario admin = admins.get(0);
        Usuario postor1 = users.get(0);

        // Elevamos la categoría del postor1 a Platino para que no rebote por validaciones de categoría al testear
        postor1.setNivelCategoria(nivelPlatino);
        postor1.setVerificado(Usuario.EstadoVerificacion.si);
        usuarioRepository.save(postor1);

        Usuario postor2 = users.size() > 1 ? users.get(1) : postor1;


        // 4. CREAR PRODUCTOS
        Producto reloj = new Producto();
        reloj.setFecha(LocalDate.now().minusDays(10));
        reloj.setEstado(Producto.EstadoProducto.INCLUIDO_EN_SUBASTA);
        reloj.setDescripcionCompleta("Reloj Rolex Submariner Edición Limitada. Acero inoxidable y oro.");
        reloj.setPropietarioUsuario(postor2);
        reloj.setPolizaSeguro("POL-RLX-9988");
        reloj.setAseguradora("Zurich Seguros");
        reloj.setMontoAsegurado(new BigDecimal("12000.00"));
        reloj.setPrecioPropuesto(new BigDecimal("10000.00"));
        reloj.setOrigenLicitoDeclarado(true);
        reloj.setPropietarioDeclarado(true);

        Producto cuadro = new Producto();
        cuadro.setFecha(LocalDate.now().minusDays(5));
        cuadro.setEstado(Producto.EstadoProducto.INCLUIDO_EN_SUBASTA);
        cuadro.setDescripcionCompleta("Cuadro original de Benito Quinquela Martín - 'Día de Trabajo'.");
        cuadro.setPropietarioUsuario(admin);
        cuadro.setPolizaSeguro("POL-ART-1122");
        cuadro.setAseguradora("Allianz");
        cuadro.setMontoAsegurado(new BigDecimal("45000.00"));
        cuadro.setPrecioPropuesto(new BigDecimal("50000.00"));
        cuadro.setArtista("Benito Quinquela Martín");
        cuadro.setOrigenLicitoDeclarado(true);
        cuadro.setPropietarioDeclarado(true);

        Producto jarron = new Producto();
        jarron.setFecha(LocalDate.now().minusDays(2));
        jarron.setEstado(Producto.EstadoProducto.INCLUIDO_EN_SUBASTA);
        jarron.setDescripcionCompleta("Jarrón de porcelana fina, Dinastía Ming. Siglo XV.");
        jarron.setPropietarioUsuario(postor2);
        jarron.setPolizaSeguro("POL-MNG-5544");
        jarron.setAseguradora("La Caja");
        jarron.setMontoAsegurado(new BigDecimal("80000.00"));
        jarron.setPrecioPropuesto(new BigDecimal("85000.00"));
        jarron.setOrigenLicitoDeclarado(true);
        jarron.setPropietarioDeclarado(true);

        // Productos de prueba para ejercitar el flujo de aprobación de punta a punta
        Producto pendienteInspeccion = new Producto();
        pendienteInspeccion.setFecha(LocalDate.now());
        pendienteInspeccion.setEstado(Producto.EstadoProducto.PENDIENTE_INSPECCION);
        pendienteInspeccion.setDescripcionCompleta("Espada ceremonial japonesa (Katana), período Edo.");
        pendienteInspeccion.setPropietarioUsuario(postor1);
        pendienteInspeccion.setMontoAsegurado(new BigDecimal("30000.00"));
        pendienteInspeccion.setOrigenLicitoDeclarado(true);
        pendienteInspeccion.setPropietarioDeclarado(true);

        Producto propuestaEnviada = new Producto();
        propuestaEnviada.setFecha(LocalDate.now().minusDays(1));
        propuestaEnviada.setEstado(Producto.EstadoProducto.PROPUESTA_ENVIADA);
        propuestaEnviada.setDescripcionCompleta("Escultura de bronce, autor anónimo, circa 1920.");
        propuestaEnviada.setPropietarioUsuario(postor1);
        propuestaEnviada.setMontoAsegurado(new BigDecimal("20000.00"));
        propuestaEnviada.setPrecioPropuesto(new BigDecimal("22000.00"));
        propuestaEnviada.setOrigenLicitoDeclarado(true);
        propuestaEnviada.setPropietarioDeclarado(true);

        Producto aceptadoPorUsuario = new Producto();
        aceptadoPorUsuario.setFecha(LocalDate.now().minusDays(2));
        aceptadoPorUsuario.setEstado(Producto.EstadoProducto.ACEPTADO_POR_USUARIO);
        aceptadoPorUsuario.setDescripcionCompleta("Colección de monedas de plata, siglo XIX.");
        aceptadoPorUsuario.setPropietarioUsuario(postor1);
        aceptadoPorUsuario.setMontoAsegurado(new BigDecimal("15000.00"));
        aceptadoPorUsuario.setPrecioPropuesto(new BigDecimal("16000.00"));
        aceptadoPorUsuario.setOrigenLicitoDeclarado(true);
        aceptadoPorUsuario.setPropietarioDeclarado(true);

        productoRepository.saveAll(List.of(reloj, cuadro, jarron, pendienteInspeccion, propuestaEnviada, aceptadoPorUsuario));


        // ====================================================================
        // ESCENARIO 1: SUBASTA FINALIZADA (Para probar historial y métricas)
        // ====================================================================

        Subasta subastaFin = new Subasta();
        subastaFin.setFecha(LocalDate.now().minusDays(3));
        subastaFin.setHora(LocalTime.of(10, 0));
        subastaFin.setEstado(Subasta.EstadoSubasta.cerrada);
        subastaFin.setCreadorUsuario(admin);
        subastaFin.setUbicacion("Sede Central UADE - Subasta Pasada");
        subastaFin.setCapacidadAsistentes(100);
        subastaFin.setTieneDeposito(Subasta.OpcionSiNo.si);
        subastaFin.setSeguridadPropia(Subasta.OpcionSiNo.si);
        subastaFin.setCategoria(Subasta.CategoriaSubasta.oro);
        subastaFin = subastaRepository.save(subastaFin);

        Catalogo catFin = new Catalogo();
        catFin.setDescripcion("Catálogo de Relojería Antigua");
        catFin.setSubasta(subastaFin);
        catFin.setCreadorUsuario(admin);
        catFin = catalogoRepository.save(catFin);

        ItemCatalogo itemReloj = new ItemCatalogo();
        itemReloj.setCatalogo(catFin);
        itemReloj.setProducto(reloj);
        itemReloj.setPrecioBase(new BigDecimal("10000.00"));
        itemReloj.setComision(new BigDecimal("1000.00"));
        itemReloj.setSubastado(ItemCatalogo.subastado_bool.si); // Ya subastado
        itemReloj = itemCatalogoRepository.save(itemReloj);

        // Registrar asistentes
        Asistente asis1 = new Asistente(null, 1, postor1, subastaFin);
        if (postor2 != postor1) {
            Asistente asis2 = new Asistente(null, 2, postor2, subastaFin);
            asistenteRepository.saveAll(List.of(asis1, asis2));
        } else {
            asistenteRepository.save(asis1);
        }

        // Generar Pujas
        Puja puja1 = new Puja(null, asis1, itemReloj, new BigDecimal("10500.00"), Puja.EstadoGanador.no);
        Puja puja2 = new Puja(null, asis1, itemReloj, new BigDecimal("11000.00"), Puja.EstadoGanador.si); // Ganadora
        pujaRepository.saveAll(List.of(puja1, puja2));

        // Generar Registro de Venta Oficial
        RegistroSubasta registroVenta = new RegistroSubasta();
        registroVenta.setSubasta(subastaFin);
        registroVenta.setPropietarioUsuario(reloj.getPropietarioUsuario());
        registroVenta.setProducto(reloj);
        registroVenta.setCompradorUsuario(postor2);
        registroVenta.setImporte(puja2.getImporte());
        registroVenta.setComision(itemReloj.getComision());
        registroSubastaRepository.save(registroVenta);


        // ====================================================================
        // ESCENARIO 2: SUBASTA LISTA PARA TESTEAR (Para probar el Timer y WS)
        // ====================================================================

        Subasta subastaNueva = new Subasta();
        subastaNueva.setFecha(LocalDate.now().plusDays(1));
        subastaNueva.setHora(LocalTime.of(20, 0));
        subastaNueva.setEstado(Subasta.EstadoSubasta.cerrada); // Dejar cerrada para abrirla manual
        subastaNueva.setCreadorUsuario(admin);
        subastaNueva.setUbicacion("Virtual / Streaming");
        subastaNueva.setCapacidadAsistentes(500);
        subastaNueva.setTieneDeposito(Subasta.OpcionSiNo.no);
        subastaNueva.setSeguridadPropia(Subasta.OpcionSiNo.si);
        subastaNueva.setCategoria(Subasta.CategoriaSubasta.platino);
        subastaNueva = subastaRepository.save(subastaNueva);

        Catalogo catNuevo = new Catalogo();
        catNuevo.setDescripcion("Catálogo Gran Subasta de Arte y Antigüedades");
        catNuevo.setSubasta(subastaNueva);
        catNuevo.setCreadorUsuario(admin);
        catNuevo = catalogoRepository.save(catNuevo);

        ItemCatalogo itemCuadro = new ItemCatalogo();
        itemCuadro.setCatalogo(catNuevo);
        itemCuadro.setProducto(cuadro);
        itemCuadro.setPrecioBase(new BigDecimal("50000.00")); // Tasado por el admin
        itemCuadro.setComision(new BigDecimal("5000.00"));
        itemCuadro.setSubastado(ItemCatalogo.subastado_bool.no); // Aún no subastado

        ItemCatalogo itemJarron = new ItemCatalogo();
        itemJarron.setCatalogo(catNuevo);
        itemJarron.setProducto(jarron);
        itemJarron.setPrecioBase(new BigDecimal("85000.00"));
        itemJarron.setComision(new BigDecimal("8500.00"));
        itemJarron.setSubastado(ItemCatalogo.subastado_bool.no); // Aún no subastado

        itemCatalogoRepository.saveAll(List.of(itemCuadro, itemJarron));

        // Dejamos al postor1 registrado como asistente para que pueda pujar ni bien empiece
        Asistente asisNuevaSubasta = new Asistente(null, 25, postor1, subastaNueva);
        asistenteRepository.save(asisNuevaSubasta);

        System.out.println("==========================================================");
        System.out.println("DB Poblada exitosamente.");
        System.out.println("Subasta Activa ID: " + subastaNueva.getId() + " lista para abrir.");
        System.out.println("Items a rematar ID: " + itemCuadro.getId() + " y " + itemJarron.getId());
        System.out.println("==========================================================");
    }
}