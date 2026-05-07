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
    private DisponibilidadProducto disponible = DisponibilidadProducto.si;

    private String descripcionCatalogo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcionCompleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_usuario_id", nullable = false)
    private Usuario propietarioUsuario;

    private String seguro;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;

    public enum DisponibilidadProducto {
        si, no
    }
}
