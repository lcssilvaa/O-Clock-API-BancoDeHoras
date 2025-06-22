package com.oclock.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateUpdateDTO {

    @NotBlank(message = "O nome completo é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "O email é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String password; // Hash SHA-256 já gerado pelo JavaFX

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotNull(message = "A jornada diária de horas é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "A jornada diária de horas deve ser maior que zero")
    private BigDecimal jornadaDiariaHoras;

    @NotBlank(message = "A permissão é obrigatória")
    @Pattern(regexp = "admin|usuario", message = "A permissão deve ser 'admin' ou 'usuario'")
    private String permissao; // String para 'admin' ou 'usuario'

    @NotNull(message = "O status ativo é obrigatório")
    private Boolean active; // Use Boolean wrapper para permitir nulo na validação, se necessário, ou boolean primitivo

    @DecimalMin(value = "0.0", inclusive = true, message = "O valor da hora não pode ser negativo")
    private BigDecimal valorHora; // Novo campo
}