package com.oclock.api.service;

import com.oclock.api.model.RegistrosPonto; // Ou RegistrosPonto, dependendo do que você decidiu usar
import com.oclock.api.dto.BankedHoursReportDTO;
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface RegistrosPontoService { // Certifique-se que é 'interface'
    List<RegistrosPonto> getRegistrosPontoByUserIdAndDate(Integer userId, LocalDate date);
    BankedHoursReportDTO generateMonthlyBankedHoursReport(Integer userId, int ano, int mes);
}