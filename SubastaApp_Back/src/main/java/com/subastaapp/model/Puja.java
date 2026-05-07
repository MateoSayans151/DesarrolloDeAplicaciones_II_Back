package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pujas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asistente_id", nullable = false)
    private Asistente asistente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemCatalogo item;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGanador ganador = EstadoGanador.no;

    public enum EstadoGanador {
        si, no
    }
}
