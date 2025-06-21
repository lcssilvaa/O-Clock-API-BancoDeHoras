package com.oclock.api.service;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.dto.BankedHoursReportDTO;
import java.time.LocalDate;
import java.util.List;

public interface RegistrosPontoService {
    List<RegistrosPonto> getRegistrosPontoByUserIdAndDate(Integer userId, LocalDate date);
    BankedHoursReportDTO generateMonthlyBankedHoursReport(Integer userId, int ano, int mes);
    BankedHoursAccumulatedReportDTO generateAccumulatedBankedHoursReport(Integer userId);
}