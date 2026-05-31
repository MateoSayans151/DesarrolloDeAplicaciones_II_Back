package com.subastaapp.seeder;

import com.subastaapp.model.Usuario;
import com.subastaapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminDoc = "00000000";

        if (usuarioRepository.findByDocumento(adminDoc).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setDocumento(adminDoc);
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setDomicilio("Oficina Central");
            admin.setFotoDocumentoFrente("N/A");
            admin.setFotoDocumentoDorso("N/A");
            admin.setPais("Argentina");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Usuario.User_roles.ADMIN);

            admin.setVerificado(Usuario.EstadoVerificacion.si);

            usuarioRepository.save(admin);
            System.out.println("Creado usuario admin, documento: 00000000, password: admin123");
        }
    }
}