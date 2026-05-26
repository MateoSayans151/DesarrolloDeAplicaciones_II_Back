package com.subastaapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "items_catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_id", nullable = false)
    private Catalogo catalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal comision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "subastado")
    private subastado_bool subastado;

    public enum subastado_bool{
        si, no
    }
}
