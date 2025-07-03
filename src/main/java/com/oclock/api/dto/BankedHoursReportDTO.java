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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankedHoursReportDTO {
    private Integer userId;
    private String userName;
    private int year;
    private int month;
    private double expectedDailyHours;
    private Map<String, Duration> dailyHoursWorked;
    private Duration totalHoursWorkedMonth;
    private Duration totalExpectedHoursMonth;
    private Duration balanceHoursMonth;
    private String balanceStatus;

    public void setDailyHoursWorked(Map<LocalDate, Duration> dailyHours) {
        this.dailyHoursWorked = dailyHours.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(DateTimeFormatter.ISO_DATE),
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}