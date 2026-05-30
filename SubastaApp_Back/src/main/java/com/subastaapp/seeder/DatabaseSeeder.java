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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // Crear usuario por defecto ya que la DB está vacía (especialmente en H2)
        Usuario lucas = new Usuario();
        lucas.setDocumento("12345678");
        lucas.setNombre("Lucas");
        lucas.setApellido("UADE");
        lucas.setPais("Argentina");
        lucas.setDomicilio("Lima 757, CABA");
        lucas.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu"); // "admin" en BCrypt
        lucas.setVerificado(Usuario.EstadoVerificacion.si);
        lucas.setFotoDocumentoFrente("pfp_frente");
        lucas.setFotoDocumentoDorso("pfp_dorso");
        lucas.setCalificacionRiesgo(1);
        lucas.setCategoria(Usuario.CategoriaUsuario.comun);
        
        lucas = usuarioRepository.save(lucas);

        Producto juegoTe = new Producto();
        juegoTe.setFecha(LocalDate.now());
        juegoTe.setEstado(Producto.EstadoProducto.ACEPTADO);
        juegoTe.setDescripcionCompleta("Juego de Té de 18 piezas de porcelana inglesa del siglo XIX en perfecto estado de conservación.");
        juegoTe.setPropietarioUsuario(lucas);
        juegoTe.setPolizaSeguro("POLIZA-12345");
        juegoTe.setAseguradora("La Holando Sudamericana");
        juegoTe.setMontoAsegurado(new BigDecimal("150000.00"));
        juegoTe.setOrigenLicitoDeclarado(true);
        juegoTe.setPropietarioDeclarado(true);

        Producto cuadro = new Producto();
        cuadro.setFecha(LocalDate.now());
        cuadro.setEstado(Producto.EstadoProducto.ACEPTADO);
        cuadro.setDescripcionCompleta("Pintura al óleo original, firmada por el autor con marco de roble macizo restaurado.");
        cuadro.setPropietarioUsuario(lucas);
        cuadro.setPolizaSeguro("POLIZA-67890");
        cuadro.setAseguradora("Allianz");
        cuadro.setMontoAsegurado(new BigDecimal("350000.00"));
        cuadro.setOrigenLicitoDeclarado(true);
        cuadro.setPropietarioDeclarado(true);

        productoRepository.saveAll(List.of(juegoTe, cuadro));

        Subasta subasta = new Subasta();
        subasta.setFecha(LocalDate.now().plusDays(15));
        subasta.setHora(LocalTime.of(18, 0));
        subasta.setEstado(Subasta.EstadoSubasta.abierta);
        subasta.setCreadorUsuario(lucas);
        subasta.setUbicacion("Salón Central, Av. Libertador 1234, CABA");
        subasta.setCapacidadAsistentes(150);
        subasta.setTieneDeposito(Subasta.OpcionSiNo.si);
        subasta.setSeguridadPropia(Subasta.OpcionSiNo.si);
        subasta.setCategoria(Subasta.CategoriaSubasta.plata);

        subasta = subastaRepository.save(subasta);

        Catalogo catalogo = new Catalogo();
        catalogo.setDescripcion("Catálogo de Arte y Antigüedades de la temporada de Otoño");
        catalogo.setSubasta(subasta);
        catalogo.setCreadorUsuario(lucas);

        catalogo = catalogoRepository.save(catalogo);

        ItemCatalogo item1 = new ItemCatalogo();
        item1.setCatalogo(catalogo);
        item1.setProducto(juegoTe);
        item1.setPrecioBase(new BigDecimal("150000.00"));
        item1.setComision(new BigDecimal("15000.00"));
        item1.setSubastado(ItemCatalogo.subastado_bool.no);

        ItemCatalogo item2 = new ItemCatalogo();
        item2.setCatalogo(catalogo);
        item2.setProducto(cuadro);
        item2.setPrecioBase(new BigDecimal("350000.00"));
        item2.setComision(new BigDecimal("35000.00"));
        item2.setSubastado(ItemCatalogo.subastado_bool.no);

        itemCatalogoRepository.saveAll(List.of(item1, item2));

        System.out.println("db populada");
    }
}

