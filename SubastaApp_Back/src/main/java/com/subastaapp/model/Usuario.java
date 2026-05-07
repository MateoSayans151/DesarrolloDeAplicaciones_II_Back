package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String documento;

    @Column(nullable = false)
    private String nombre;

    private String direccion;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String fotoBase64;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVerificacion verificado = EstadoVerificacion.no;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion verificacionFinanciera;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion verificacionJudicial;

    private Integer calificacionRiesgo;

    @Enumerated(EnumType.STRING)
    private CategoriaUsuario categoria;

    public enum EstadoVerificacion {
        si, no
    }

    public enum CategoriaUsuario {
        comun, especial, plata, oro, platino
    }
}
