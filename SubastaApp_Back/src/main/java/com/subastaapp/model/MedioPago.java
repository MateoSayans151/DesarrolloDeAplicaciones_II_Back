package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medios_pago")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_medio_pago", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVerificacion verificado = EstadoVerificacion.no;

    public enum EstadoVerificacion {
        si, no, pendiente
    }

}
