package com.oclock.api.model; // Ajuste o pacote

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_ponto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrosPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer id;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "data_hora_registro", nullable = false)
    private LocalDateTime dataHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false)
    private TipoRegistro tipoRegistro;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
