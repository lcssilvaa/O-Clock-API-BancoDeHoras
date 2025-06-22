package com.oclock.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Data // Gera automaticamente getters, setters, toString(), equals() e hashCode()
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos

public class BankedHoursReportDTO {
    private Integer userId;
    private String userName;
    private int year;
    private int month;
    private double expectedDailyHours;
    // Map onde a chave é a data (como String para JSON) e o valor é a duração trabalhada naquele dia.
    private Map<String, Duration> dailyHoursWorked;
    private Duration totalHoursWorkedMonth; // Soma das horas trabalhadas em todos os dias do mês
    private Duration totalExpectedHoursMonth; // Soma das horas esperadas em todos os dias úteis do mês
    private Duration balanceHoursMonth; // Saldo: (totalHorasTrabalhadas - totalHorasEsperadas)
    private String balanceStatus; // Status do saldo: "POSITIVO", "NEGATIVO", "ZERADO"

    /**
     * Setter personalizado para 'dailyHoursWorked'.
     * Ele converte o Map original (com LocalDate como chave) para um Map (com String como chave)
     * para facilitar a serialização para JSON, pois LocalDate não é naturalmente serializável em JSON da forma que queremos.
     */
    public void setDailyHoursWorked(Map<LocalDate, Duration> dailyHours) {
        this.dailyHoursWorked = dailyHours.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(DateTimeFormatter.ISO_DATE), // Converte LocalDate para String (YYYY-MM-DD)
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}