package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asistentes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"subasta_id", "usuario_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asistente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numeroPostor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subasta_id", nullable = false)
    private Subasta subasta;
}
