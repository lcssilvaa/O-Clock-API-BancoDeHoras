package com.oclock.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data //Lombok: Gera automaticamente getters, setters, toString(), equals() e hashCode()
@NoArgsConstructor //Lombok: Gera um construtor sem argumentos
@AllArgsConstructor //Lombok: Gera um construtor com todos os argumentos
public class BankedHoursAccumulatedReportDTO {

    private Integer userId;
    private String userName;

    private Duration totalAccumulatedBalance;

    private List<BankedHoursReportDTO> monthlySummaries;
}