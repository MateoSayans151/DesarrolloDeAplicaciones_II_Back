package com.subastaapp.businesslogic;

import com.subastaapp.model.Subasta;
import com.subastaapp.repository.SubastaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubastaScheduler {

    private final SubastaRepository subastaRepository;
    private final SubastaService subastaService;

    @Scheduled(fixedRate = 60000)
    public void abrirSubastasProgramadas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Subasta> programadas = subastaRepository.findByEstado(Subasta.EstadoSubasta.programada);
        for (Subasta subasta : programadas) {
            LocalDateTime fechaHora = LocalDateTime.of(subasta.getFecha(), subasta.getHora());
            if (!fechaHora.isAfter(ahora)) {
                System.out.println("SCHEDULER: Abriendo subasta " + subasta.getId() + " programada para " + fechaHora);
                try {
                    subastaService.cambiarEstado(subasta.getId(), "abierta");
                } catch (Exception e) {
                    System.err.println("SCHEDULER: Error al abrir subasta " + subasta.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}
