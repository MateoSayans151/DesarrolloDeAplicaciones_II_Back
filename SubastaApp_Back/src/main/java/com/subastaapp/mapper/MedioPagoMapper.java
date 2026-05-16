package com.subastaapp.mapper;

import com.subastaapp.dto.request.ChequeCertificadoRequest;
import com.subastaapp.dto.request.CuentaBancariaRequest;
import com.subastaapp.dto.request.MedioPagoRequest;
import com.subastaapp.dto.request.TarjetaCreditoRequest;
import com.subastaapp.model.*;
import org.springframework.stereotype.Component;

@Component
public class MedioPagoMapper {

    public MedioPago toEntity(MedioPagoRequest dto, Usuario usuario) {

        if (dto instanceof TarjetaCreditoRequest tcReq) {
            TarjetaCredito tc = new TarjetaCredito();
            tc.setNumeroTarjeta(tcReq.getNumeroTarjeta());
            tc.setNombreTitular(tcReq.getNombreTitular());
            tc.setFechaVencimiento(tcReq.getFechaVencimento());
            tc.setCvv(tcReq.getCvv());
            tc.setUsuario(usuario);
            tc.setVerificado(MedioPago.EstadoVerificacion.no);
            return tc;

        } else if (dto instanceof CuentaBancariaRequest cbReq) {
            CuentaBancaria cb = new CuentaBancaria();
            cb.setTitularCuenta(cbReq.getTitularCuenta());
            cb.setTipoCuenta(cbReq.getTipoCuenta());
            cb.setMoneda(cbReq.getMoneda());
            cb.setUsuario(usuario);
            cb.setVerificado(MedioPago.EstadoVerificacion.no);
            return cb;

        } else if (dto instanceof ChequeCertificadoRequest chReq) {
            ChequeCertificado ch = new ChequeCertificado();
            ch.setMonto(chReq.getMonto());
            ch.setFotoTitularCheque(chReq.getFotoTitularCheque());
            ch.setFechaEmision(chReq.getFechaEmision());
            ch.setBancoEmisor(chReq.getBancoEmisor());
            ch.setTitularCheque(chReq.getTitularCheque());
            ch.setNumeroCheque(chReq.getNumeroCheque());
            ch.setUsuario(usuario);
            ch.setVerificado(MedioPago.EstadoVerificacion.no);
            return ch;
        }

        throw new IllegalArgumentException("Tipo de medio de pago no soportado");
    }
}
