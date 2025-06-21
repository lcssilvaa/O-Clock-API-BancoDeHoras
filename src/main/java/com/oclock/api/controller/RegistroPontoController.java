package com.oclock.api.controller;

import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.service.RegistrosPontoService;
import com.oclock.api.dto.BankedHoursReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ponto")
public class RegistroPontoController {

    private final RegistrosPontoService registrosPontoService;

    @Autowired
    public RegistroPontoController(RegistrosPontoService registrosPontoService) {
        this.registrosPontoService = registrosPontoService;
    }

    @GetMapping("/{userId}/dia")
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByDay(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByUserIdAndDate(userId, date);
        return ResponseEntity.ok(registros);
    }

    /**
     * Endpoint para gerar o relatório completo do banco de horas mensal de um usuário.
     * GET /api/ponto/{userId}/banco-horas-mensal?ano=YYYY&mes=MM
     */
    @GetMapping("/{userId}/banco-horas-mensal")
    public ResponseEntity<BankedHoursReportDTO> getMonthlyBankedHoursReport(
            @PathVariable Integer userId,
            @RequestParam int ano,
            @RequestParam int mes) {
        BankedHoursReportDTO report = registrosPontoService.generateMonthlyBankedHoursReport(userId, ano, mes);
        return ResponseEntity.ok(report);
    }
}