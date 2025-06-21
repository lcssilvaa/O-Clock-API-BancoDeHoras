package com.oclock.api.controller;

import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.service.RegistrosPontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ponto") // This path still makes sense for point-related data
public class RegistroPontoController {

    private final RegistrosPontoService registroPontoService;

    @Autowired
    public RegistroPontoController(RegistrosPontoService registroPontoService) {
        this.registroPontoService = registroPontoService;
    }

    /**
     * Endpoint para obter os registros de ponto de um usuário para um dia específico.
     * GET /api/ponto/{userId}/dia?date=YYYY-MM-DD
     * Útil para mostrar o detalhe das batidas do dia no extrato do banco de horas.
     */
    @GetMapping("/{userId}/dia")
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByDay(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RegistrosPonto> registros = registroPontoService.getRegistrosPontoByUserIdAndDate(userId, date);
        return ResponseEntity.ok(registros);
    }

    /**
     * Endpoint para calcular as horas trabalhadas por dia para um usuário em um mês.
     * GET /api/ponto/{userId}/horas-mensais?ano=YYYY&mes=MM&jornada=8.0
     * Esta é uma parte crucial para o banco de horas.
     */
    @GetMapping("/{userId}/horas-mensais")
    public ResponseEntity<Map<LocalDate, Duration>> getHorasTrabalhadasMensais(
            @PathVariable Integer userId,
            @RequestParam int ano,
            @RequestParam int mes,
            @RequestParam(defaultValue = "8.0") double jornada) {
        Map<LocalDate, Duration> horasPorDia = registroPontoService.calcularHorasTrabalhadasMensais(userId, ano, mes, jornada);
        return ResponseEntity.ok(horasPorDia);
    }
}