package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "subastas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSubasta estado = EstadoSubasta.cerrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_usuario_id", nullable = false)
    private Usuario creadorUsuario;

    private String ubicacion;

    private Integer capacidadAsistentes;

    @OneToOne(mappedBy = "subasta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Catalogo catalogo;

    @Enumerated(EnumType.STRING)
    private OpcionSiNo tieneDeposito;

    @Enumerated(EnumType.STRING)
    private OpcionSiNo seguridadPropia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaSubasta categoria;

    public enum EstadoSubasta {
        abierta, cerrada
    }

    public enum CategoriaSubasta {
        comun, especial, plata, oro, platino
    }

    public enum OpcionSiNo {
        si, no
    }
}
