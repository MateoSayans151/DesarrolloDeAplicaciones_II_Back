package com.subastaapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("CUENTA_BANCARIA")
@Data
public class CuentaBancaria extends MedioPago{
    private String titularCuenta;
    private String tipoCuenta;
    private String moneda;
    private String cbuAlias;
}
