package com.subastaapp.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo" // El front-end DEBE mandar este campo en el JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TarjetaCreditoRequest.class, name = "TARJETA"),
        @JsonSubTypes.Type(value = CuentaBancariaRequest.class, name = "CUENTA"),
        @JsonSubTypes.Type(value = ChequeCertificadoRequest.class, name = "CHEQUE")
})
public abstract class MedioPagoRequest {
}
