package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado = EstadoProducto.PENDIENTE;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcionCompleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_usuario_id", nullable = false)
    private Usuario propietarioUsuario;

    // Campos de Seguro mejorados según consigna
    private String polizaSeguro;
    private String aseguradora;
    private java.math.BigDecimal montoAsegurado;

    // Metadata del bien (UADE requirements)
    private String artista;
    private String disenador;
    
    @Column(columnDefinition = "TEXT")
    private String historia;
    
    private String ubicacionDeposito;

    // Declaraciones obligatorias
    private Boolean origenLicitoDeclarado = false;
    private Boolean propietarioDeclarado = false;

    @Column(columnDefinition = "TEXT")
    private String motivoRechazo;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;

    public enum EstadoProducto {
        PENDIENTE,
        ACEPTADO,
        RECHAZADO,
        DEVUELTO,
        EN_SUBASTA,
        VENDIDO
    }
}
