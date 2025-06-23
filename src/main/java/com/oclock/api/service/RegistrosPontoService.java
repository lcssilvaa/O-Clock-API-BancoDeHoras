package com.oclock.api.service;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.dto.BankedHoursReportDTO;
import com.oclock.api.dto.RegistroPontoAdminDTO;
import com.oclock.api.model.RegistrosPonto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistrosPontoService {

    // --- Métodos Existentes ---
    List<RegistrosPonto> getRegistrosPontoByUserIdAndDate(Integer userId, LocalDate date);
    BankedHoursReportDTO generateMonthlyBankedHoursReport(Integer userId, int ano, int mes);
    BankedHoursAccumulatedReportDTO generateAccumulatedBankedHoursReport(Integer userId);

    RegistrosPonto baterPonto(Integer idUsuario, LocalDateTime dataHoraRegistro);

    List<RegistrosPonto> getAllRegistrosPonto();
    RegistrosPonto getRegistroPontoById(Integer id);
    RegistrosPonto createRegistroPonto(RegistroPontoAdminDTO registroPontoDTO); // Adicionado para criação explícita (via admin ou import)
    RegistrosPonto updateRegistroPonto(Integer id, RegistroPontoAdminDTO registroPontoDTO);
    void deleteRegistroPonto(Integer id);

    List<RegistrosPonto> getRegistrosPontoByUsuarioAndPeriodo(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);
    List<RegistrosPonto> getRegistrosPontoByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}