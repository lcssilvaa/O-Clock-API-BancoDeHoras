package com.oclock.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoRequestDTO {

    @NotNull(message = "A data e hora do registro são obrigatórias")
    private LocalDateTime dataHoraRegistro;

}