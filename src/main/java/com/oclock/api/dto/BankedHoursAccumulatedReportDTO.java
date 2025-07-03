package com.oclock.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankedHoursAccumulatedReportDTO {

    private Integer userId;
    private String userName;

    private Duration totalAccumulatedBalance;

    private List<BankedHoursReportDTO> monthlySummaries;
}