package com.subastaapp.businesslogic;

import com.subastaapp.exception.ConflictException;
import com.subastaapp.exception.ResourceNotFoundException;
import com.subastaapp.model.MedioPago;
import com.subastaapp.repository.MedioPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedioPagoService {

    private final MedioPagoRepository medioPagoRepository;

    public void verificarMedioPago(long id){
        MedioPago mp = medioPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un medio de pago con ese ID"));

        if(mp.getVerificado() == MedioPago.EstadoVerificacion.si){
            throw new ConflictException("El medio de pago ya se encuentra verificado.");
        }

        mp.setVerificado(MedioPago.EstadoVerificacion.si);
        medioPagoRepository.save(mp);
    }

    public void darDeBajaMedioPago(long id){
        MedioPago mp = medioPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un medio de pago con ese ID"));

        if(mp.getVerificado() == MedioPago.EstadoVerificacion.no){
            throw new ConflictException("El medio de pago ya se encuentra dado de baja.");
        }

        mp.setVerificado(MedioPago.EstadoVerificacion.no);
        medioPagoRepository.save(mp);
    }
}