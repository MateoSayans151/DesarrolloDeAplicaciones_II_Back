package com.subastaapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String documento;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String pais;

    @Column(nullable = false)
    private String domicilio;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String fotoBase64;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String fotoDocumentoFrente;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String fotoDocumentoDorso;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<MedioPago> medioPagos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVerificacion verificado = EstadoVerificacion.no;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion verificacionFinanciera;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion verificacionJudicial;

    private Integer calificacionRiesgo;


    @ManyToOne
    @JoinColumn(name = "nivel_categoria_id")
    private NivelCategoria nivelCategoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User_roles role = User_roles.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


    public enum EstadoVerificacion {
        si, no
    }


    public enum User_roles{
        USER, ADMIN
    }


    //Métodos que no vamos a usar pero los necesito por la ifaz para el tema de los roles (Spring Security te hace implementar esa interfaz si o si)

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
