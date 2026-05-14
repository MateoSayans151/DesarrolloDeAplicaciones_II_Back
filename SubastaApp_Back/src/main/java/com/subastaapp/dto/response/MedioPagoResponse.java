package com.subastaapp.dto.response;

import com.subastaapp.model.ChequeCertificado;
import com.subastaapp.model.CuentaBancaria;
import com.subastaapp.model.MedioPago;
import com.subastaapp.model.TarjetaCredito;
import lombok.Data;

@Data
public class MedioPagoResponse {
    private Long id;
    private String tipo;
    private String verificado;
    private String ultimosNumeros;

    public static MedioPagoResponse from(MedioPago mp) {
        MedioPagoResponse r = new MedioPagoResponse();
        r.setId(mp.getId());
        r.setVerificado(mp.getVerificado().name());

        if (mp instanceof TarjetaCredito){
            r.setTipo("TARJETA");
            String credito = ((TarjetaCredito) mp).getNumeroTarjeta();
            r.setUltimosNumeros(credito.substring(credito.length() - 4));
        }
        else if (mp instanceof CuentaBancaria){
            r.setTipo("CUENTA");
            String cb = ((CuentaBancaria) mp).getCbuAlias();
            r.setUltimosNumeros(cb.substring(cb.length() - 4));
        }
        else if (mp instanceof ChequeCertificado) {
            r.setTipo("CHEQUE");
            String cc =  (((ChequeCertificado) mp).getNumeroCheque());
            r.setUltimosNumeros(cc.substring(cc.length() - 4));
        }

        return r;
    }
}
