package com.oclock.api.model; // Ajuste o nome do pacote conforme seu projeto

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity // Marca esta classe como uma entidade JPA
@Table(name = "usuarios") // Mapeia para a tabela 'usuarios' no seu banco de dados
@Data // Lombok: Gera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Gera construtor sem argumentos
@AllArgsConstructor // Lombok: Gera construtor com todos os argumentos
public class User {

    @Id // Marca como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento para MySQL
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nome_completo", nullable = false)
    private String fullName;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "permissao", nullable = false)
    private String permission;

    @Column(name = "ativo", nullable = false)
    private boolean active;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_atualizacao")
    private LocalDateTime updatedAt;

    @Column(name = "valor_hora", nullable = false)
    private Double valorHora; // Use Double para colunas DECIMAL no JPA

}