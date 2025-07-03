package com.oclock.api.controller;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.dto.BankedHoursReportDTO;
import com.oclock.api.dto.PontoRequestDTO;
import com.oclock.api.dto.RegistroPontoAdminDTO;
import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.service.RegistrosPontoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "27.0.0.1:http://15500")
@RestController
@RequestMapping("/api/ponto")
public class RegistroPontoController {

    private final RegistrosPontoService registrosPontoService;

    @Autowired
    public RegistroPontoController(RegistrosPontoService registrosPontoService) {
        this.registrosPontoService = registrosPontoService;
    }

    @PostMapping("/bater/{idUsuario}")
    public ResponseEntity<RegistrosPonto> baterPonto(@PathVariable Integer idUsuario,
                                                     @Valid @RequestBody PontoRequestDTO pontoRequestDTO) {
        RegistrosPonto novoPonto = registrosPontoService.baterPonto(idUsuario, pontoRequestDTO.getDataHoraRegistro());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPonto);
    }

    @GetMapping("/{userId}/dia")
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByDay(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByUserIdAndDate(userId, date);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/usuario/{userId}/periodo")
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByUsuarioAndPeriodo(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByUsuarioAndPeriodo(userId, inicio, fim);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByPeriodo(inicio, fim);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{userId}/banco-horas-mensal")
    public ResponseEntity<BankedHoursReportDTO> getMonthlyBankedHoursReport(
            @PathVariable Integer userId,
            @RequestParam int ano,
            @RequestParam int mes) {
        BankedHoursReportDTO report = registrosPontoService.generateMonthlyBankedHoursReport(userId, ano, mes);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{userId}/banco-horas-acumulado")
    public ResponseEntity<BankedHoursAccumulatedReportDTO> getAccumulatedBankedHoursReport(
            @PathVariable Integer userId) {
        BankedHoursAccumulatedReportDTO report = registrosPontoService.generateAccumulatedBankedHoursReport(userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<RegistrosPonto>> getAllRegistrosPonto() {
        List<RegistrosPonto> registros = registrosPontoService.getAllRegistrosPonto();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrosPonto> getRegistroPontoById(@PathVariable Integer id) {
        RegistrosPonto registro = registrosPontoService.getRegistroPontoById(id);
        return ResponseEntity.ok(registro);
    }

    @PostMapping
    public ResponseEntity<RegistrosPonto> createRegistroPonto(@Valid @RequestBody RegistroPontoAdminDTO registroPontoDTO) {
        RegistrosPonto novoRegistro = registrosPontoService.createRegistroPonto(registroPontoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrosPonto> updateRegistroPonto(@PathVariable Integer id,
                                                              @Valid @RequestBody RegistroPontoAdminDTO registroPontoDTO) {
        RegistrosPonto registroAtualizado = registrosPontoService.updateRegistroPonto(id, registroPontoDTO);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistroPonto(@PathVariable Integer id) {
        registrosPontoService.deleteRegistroPonto(id);
    }
}