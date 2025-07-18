package com.oclock.api.dto;

import com.oclock.api.model.TipoRegistro;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroPontoAdminDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Integer idUsuario;

    @NotNull(message = "A data e hora do registro são obrigatórias")
    private LocalDateTime dataHoraRegistro;

    @NotNull(message = "O tipo de registro é obrigatório (ENTRADA ou SAIDA)")
    private TipoRegistro tipoRegistro;

    private String observacao;
}