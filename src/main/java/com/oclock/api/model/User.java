package com.oclock.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "permissao", nullable = false)
    private String permissao;

    @Column(name = "ativo", nullable = false)
    private boolean active;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_atualizacao")
    private LocalDateTime updatedAt;

    @Column(name = "valor_hora", precision = 10, scale = 2)
    private BigDecimal valorHora;

    @Column(name = "jornada_diaria_horas", precision = 4, scale = 2, nullable = false)
    private BigDecimal jornadaDiariaHoras;

    }
