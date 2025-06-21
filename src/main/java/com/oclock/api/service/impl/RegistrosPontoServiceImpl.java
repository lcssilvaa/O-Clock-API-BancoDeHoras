package com.oclock.api.service.impl;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.model.RegistrosPonto; // Verifique se o nome da sua entidade é este mesmo
import com.oclock.api.model.User;
import com.oclock.api.repository.RegistroPontoRepository;
import com.oclock.api.repository.UserRepository;
import com.oclock.api.service.RegistrosPontoService; // Verifique se o nome da sua interface de serviço é este mesmo
import com.oclock.api.dto.BankedHoursReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service // Marca a classe como um componente de serviço do Spring.
public class RegistrosPontoServiceImpl implements RegistrosPontoService {

    private final RegistroPontoRepository registroPontoRepository;
    private final UserRepository userRepository;

    @Autowired // Injeta as dependências no construtor.
    public RegistrosPontoServiceImpl(RegistroPontoRepository registroPontoRepository, UserRepository userRepository) {
        this.registroPontoRepository = registroPontoRepository;
        this.userRepository = userRepository;
    }

    /**
     * Recupera registros de ponto de um usuário para um dia específico.
     */
    @Override
    public List<RegistrosPonto> getRegistrosPontoByUserIdAndDate(Integer userId, LocalDate date) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + userId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return registroPontoRepository.findByIdUsuarioAndDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(userId, startOfDay, endOfDay);
    }

    /**
     * Gera um relatório mensal de banco de horas para um usuário.
     * Calcula horas trabalhadas, horas esperadas e o saldo.
     * Assume jornada de segunda a sexta para horas esperadas.
     */
    @Override
    public BankedHoursReportDTO generateMonthlyBankedHoursReport(Integer userId, int ano, int mes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + userId));

        double expectedDailyHours = user.getJornadaDiariaHoras() != null ? user.getJornadaDiariaHoras().doubleValue() : 8.0;

        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.with(TemporalAdjusters.lastDayOfMonth());

        List<RegistrosPonto> registrosDoMes = registroPontoRepository.findByIdUsuarioAndDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(
                userId, inicioMes.atStartOfDay(), fimMes.atTime(LocalTime.MAX)
        );

        Map<LocalDate, List<RegistrosPonto>> registrosPorDia = registrosDoMes.stream()
                .collect(Collectors.groupingBy(
                        registro -> registro.getDataHoraRegistro().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        Map<LocalDate, Duration> horasTrabalhadasPorDia = new LinkedHashMap<>();
        Duration totalHorasTrabalhadasNoMes = Duration.ZERO;
        Duration totalHorasEsperadasNoMes = Duration.ZERO;

        for (LocalDate dia = inicioMes; !dia.isAfter(fimMes); dia = dia.plusDays(1)) {
            // Conta as horas esperadas apenas em dias de semana (segunda a sexta).
            if (dia.getDayOfWeek() != DayOfWeek.SATURDAY && dia.getDayOfWeek() != DayOfWeek.SUNDAY) {
                totalHorasEsperadasNoMes = totalHorasEsperadasNoMes.plus(Duration.ofHours((long) expectedDailyHours));
            }

            List<RegistrosPonto> marcacoesDoDia = registrosPorDia.getOrDefault(dia, new ArrayList<>());
            marcacoesDoDia.sort(Comparator.comparing(RegistrosPonto::getDataHoraRegistro));

            Duration totalTrabalhadoNoDia = Duration.ZERO;
            LocalDateTime entrada = null;

            // Calcula horas trabalhadas para o dia (pares ENTRADA/SAÍDA).
            for (RegistrosPonto registro : marcacoesDoDia) {
                if (entrada == null) {
                    entrada = registro.getDataHoraRegistro();
                } else {
                    LocalDateTime saida = registro.getDataHoraRegistro();
                    totalTrabalhadoNoDia = totalTrabalhadoNoDia.plus(Duration.between(entrada, saida));
                    entrada = null;
                }
            }
            horasTrabalhadasPorDia.put(dia, totalTrabalhadoNoDia);
            totalHorasTrabalhadasNoMes = totalHorasTrabalhadasNoMes.plus(totalTrabalhadoNoDia);
        }

        // Calcula o saldo mensal do banco de horas.
        Duration balanceHorasMes = totalHorasTrabalhadasNoMes.minus(totalHorasEsperadasNoMes);
        String balanceStatus;
        if (balanceHorasMes.isZero()) {
            balanceStatus = "ZERADO";
        } else if (balanceHorasMes.isNegative()) {
            balanceStatus = "NEGATIVO";
        } else {
            balanceStatus = "POSITIVO";
        }

        // Popula e retorna o DTO do relatório.
        BankedHoursReportDTO report = new BankedHoursReportDTO();
        report.setUserId(user.getId());
        report.setUserName(user.getFullName());
        report.setYear(ano);
        report.setMonth(mes);
        report.setExpectedDailyHours(expectedDailyHours);
        report.setDailyHoursWorked(horasTrabalhadasPorDia); // Converte LocalDate para String no DTO
        report.setTotalHoursWorkedMonth(totalHorasTrabalhadasNoMes);
        report.setTotalExpectedHoursMonth(totalHorasEsperadasNoMes);
        report.setBalanceHoursMonth(balanceHorasMes);
        report.setBalanceStatus(balanceStatus);

        return report;
    }

    @Override
    public BankedHoursAccumulatedReportDTO generateAccumulatedBankedHoursReport(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + userId));

        // 1. Encontrar o mês e ano do primeiro registro de ponto do usuário
        Optional<RegistrosPonto> firstRecord = registroPontoRepository.findTopByIdUsuarioOrderByDataHoraRegistroDesc(userId);

        if (firstRecord.isEmpty()) {
            // Se não há registros de ponto, retorna um relatório acumulado vazio
            BankedHoursAccumulatedReportDTO emptyReport = new BankedHoursAccumulatedReportDTO();
            emptyReport.setUserId(userId);
            emptyReport.setUserName(user.getFullName());
            emptyReport.setTotalAccumulatedBalance(Duration.ZERO);
            emptyReport.setMonthlySummaries(new ArrayList<>());
            return emptyReport;
        }

        LocalDate startCalculatingDate = firstRecord.get().getDataHoraRegistro().toLocalDate();
        LocalDate today = LocalDate.now();

        Duration totalAccumulatedBalance = Duration.ZERO;
        List<BankedHoursReportDTO> monthlySummaries = new ArrayList<>();

        // 2. Iterar mês a mês, do primeiro registro até o mês atual
        LocalDate currentMonthIterator = LocalDate.of(startCalculatingDate.getYear(), startCalculatingDate.getMonth(), 1);

        // Enquanto o iterador for menor ou igual ao mês atual (para incluir o mês corrente)
        while (!currentMonthIterator.isAfter(today)) {
            int ano = currentMonthIterator.getYear();
            int mes = currentMonthIterator.getMonthValue();

            // Reutiliza o método de geração de relatório mensal
            BankedHoursReportDTO monthlyReport = generateMonthlyBankedHoursReport(userId, ano, mes);

            // Acumula o saldo do mês
            totalAccumulatedBalance = totalAccumulatedBalance.plus(monthlyReport.getBalanceHoursMonth());

            // Adiciona o relatório mensal à lista de sumários
            monthlySummaries.add(monthlyReport);

            // Move para o próximo mês
            currentMonthIterator = currentMonthIterator.plusMonths(1); // Mova esta linha para o final do loop
        }


        // 3. Popula e retorna o DTO do relatório acumulado
        BankedHoursAccumulatedReportDTO accumulatedReport = new BankedHoursAccumulatedReportDTO();
        accumulatedReport.setUserId(user.getId());
        accumulatedReport.setUserName(user.getFullName());
        accumulatedReport.setTotalAccumulatedBalance(totalAccumulatedBalance);
        accumulatedReport.setMonthlySummaries(monthlySummaries);

        return accumulatedReport;
    }
}