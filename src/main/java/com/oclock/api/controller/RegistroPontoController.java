package com.oclock.api.controller;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.dto.BankedHoursReportDTO;
import com.oclock.api.dto.PontoRequestDTO; // Importar este DTO para a requisição de 'bater ponto'
import com.oclock.api.dto.RegistroPontoAdminDTO; // Importar este DTO para o CRUD admin
import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.service.RegistrosPontoService;
import jakarta.validation.Valid; // Para validação dos DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus; // Importar para ResponseEntity.status
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime; // Importar para os métodos que usam LocalDateTime
import java.util.List;

@RestController // Marca a classe como um Controller REST
@RequestMapping("/api/ponto") // Define o prefixo base para todos os endpoints deste controller
public class RegistroPontoController {

    private final RegistrosPontoService registrosPontoService;

    @Autowired
    public RegistroPontoController(RegistrosPontoService registrosPontoService) {
        this.registrosPontoService = registrosPontoService;
    }

    // --- Endpoints de Bater Ponto ---

    @PostMapping("/bater/{idUsuario}") // Endpoint para um usuário "bater o ponto"
    public ResponseEntity<RegistrosPonto> baterPonto(@PathVariable Integer idUsuario,
                                                     @Valid @RequestBody PontoRequestDTO pontoRequestDTO) {
        // A lógica de ENTRADA/SAIDA é determinada no serviço
        RegistrosPonto novoPonto = registrosPontoService.baterPonto(idUsuario, pontoRequestDTO.getDataHoraRegistro());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPonto);
    }

    // --- Endpoints de Consulta de Registros (existentes e adicionais) ---

    @GetMapping("/{userId}/dia") // Ex: /api/ponto/1/dia?date=2025-06-22
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByDay(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByUserIdAndDate(userId, date);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/usuario/{userId}/periodo") // Ex: /api/ponto/usuario/1/periodo?inicio=2025-06-01T00:00:00&fim=2025-06-30T23:59:59
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByUsuarioAndPeriodo(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByUsuarioAndPeriodo(userId, inicio, fim);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/periodo") // Ex: /api/ponto/periodo?inicio=2025-06-01T00:00:00&fim=2025-06-30T23:59:59
    public ResponseEntity<List<RegistrosPonto>> getRegistrosPontoByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<RegistrosPonto> registros = registrosPontoService.getRegistrosPontoByPeriodo(inicio, fim);
        return ResponseEntity.ok(registros);
    }

    // --- Endpoints de Relatórios (existentes) ---

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

    @GetMapping("/{userId}/banco-horas-acumulado") // Ajustado o path para consistência com o mensal
    public ResponseEntity<BankedHoursAccumulatedReportDTO> getAccumulatedBankedHoursReport(
            @PathVariable Integer userId) {
        BankedHoursAccumulatedReportDTO report = registrosPontoService.generateAccumulatedBankedHoursReport(userId);
        return ResponseEntity.ok(report);
    }

    // --- Endpoints CRUD para Administrador (ou com permissão adequada) ---

    @GetMapping // Ex: /api/ponto (lista todos os pontos)
    public ResponseEntity<List<RegistrosPonto>> getAllRegistrosPonto() {
        List<RegistrosPonto> registros = registrosPontoService.getAllRegistrosPonto();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}") // Ex: /api/ponto/123 (busca ponto por ID)
    public ResponseEntity<RegistrosPonto> getRegistroPontoById(@PathVariable Integer id) {
        RegistrosPonto registro = registrosPontoService.getRegistroPontoById(id);
        return ResponseEntity.ok(registro);
    }

    @PostMapping // Ex: /api/ponto (cria um novo ponto, admin)
    public ResponseEntity<RegistrosPonto> createRegistroPonto(@Valid @RequestBody RegistroPontoAdminDTO registroPontoDTO) {
        RegistrosPonto novoRegistro = registrosPontoService.createRegistroPonto(registroPontoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @PutMapping("/{id}") // Ex: /api/ponto/123 (atualiza um ponto por ID, admin)
    public ResponseEntity<RegistrosPonto> updateRegistroPonto(@PathVariable Integer id,
                                                              @Valid @RequestBody RegistroPontoAdminDTO registroPontoDTO) {
        RegistrosPonto registroAtualizado = registrosPontoService.updateRegistroPonto(id, registroPontoDTO);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/{id}") // Ex: /api/ponto/123 (deleta ponto por ID, admin)
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 No Content para exclusão bem-sucedida
    public void deleteRegistroPonto(@PathVariable Integer id) {
        registrosPontoService.deleteRegistroPonto(id);
    }
}