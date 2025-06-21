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
}