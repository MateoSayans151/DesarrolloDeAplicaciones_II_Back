package com.subastaapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("CHEQUE_CERTIFICADO")
@Data
public class ChequeCertificado extends MedioPago{
    private Double monto;
    private String bancoEmisor;
    private String titularCheque;
    private String numeroCheque;
    private String fechaEmision;
    @Column(columnDefinition = "TEXT")
    private String fotoTitularCheque;
}
