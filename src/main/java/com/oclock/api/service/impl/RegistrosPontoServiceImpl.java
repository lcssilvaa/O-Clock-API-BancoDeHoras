package com.oclock.api.service.impl;

import com.oclock.api.dto.BankedHoursAccumulatedReportDTO;
import com.oclock.api.dto.BankedHoursReportDTO;
import com.oclock.api.dto.PontoRequestDTO; // Importe o DTO para bater ponto
import com.oclock.api.dto.RegistroPontoAdminDTO; // Importe o DTO para admin CRUD
import com.oclock.api.model.RegistrosPonto;
import com.oclock.api.model.TipoRegistro; // Nosso Enum TipoRegistro
import com.oclock.api.model.User;
import com.oclock.api.repository.RegistroPontoRepository;
import com.oclock.api.repository.UserRepository;
import com.oclock.api.service.RegistrosPontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import para @Transactional
import org.springframework.web.server.ResponseStatusException; // Import existente

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegistrosPontoServiceImpl implements RegistrosPontoService {

    private final RegistroPontoRepository registroPontoRepository;
    private final UserRepository userRepository;

    @Autowired
    public RegistrosPontoServiceImpl(RegistroPontoRepository registroPontoRepository, UserRepository userRepository) {
        this.registroPontoRepository = registroPontoRepository;
        this.userRepository = userRepository;
    }

    /**
     * Recupera registros de ponto de um usuário para um dia específico.
     * (Método existente)
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
     * (Método existente e completo)
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
            // IMPORTANTE: Esta lógica assume que os pontos ENTRADA/SAIDA estão corretos.
            // Para maior robustez, considere a validação ou correção de sequências inválidas de pontos.
            for (RegistrosPonto registro : marcacoesDoDia) {
                // AQUI: A sua lógica de cálculo de horas trabalhadas já está adaptada para ENTRADA/SAIDA.
                // Mas, se você decidir futuramente adicionar INICIO_INTERVALO/FIM_INTERVALO,
                // esta parte da lógica precisará ser expandida para considerar esses tipos de registro.
                if (entrada == null) {
                    // Assume que o primeiro registro válido é uma ENTRADA
                    entrada = registro.getDataHoraRegistro();
                } else {
                    // Assume que o próximo registro é uma SAIDA
                    LocalDateTime saida = registro.getDataHoraRegistro();
                    totalTrabalhadoNoDia = totalTrabalhadoNoDia.plus(Duration.between(entrada, saida));
                    entrada = null; // Reseta para próxima ENTRADA
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
        report.setUserName(user.getNomeCompleto());
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

    /**
     * Gera um relatório acumulado de banco de horas para um usuário.
     * (Método existente e completo)
     */
    @Override
    public BankedHoursAccumulatedReportDTO generateAccumulatedBankedHoursReport(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + userId));

        // Encontrar o mês e ano do primeiro registro de ponto do usuário (o mais antigo)
        Optional<RegistrosPonto> oldestRecordOpt = registroPontoRepository.findTopByIdUsuarioOrderByDataHoraRegistroAsc(userId);

        if (oldestRecordOpt.isEmpty()) {
            // Se não há registros de ponto, retorna um relatório acumulado vazio
            BankedHoursAccumulatedReportDTO emptyReport = new BankedHoursAccumulatedReportDTO();
            emptyReport.setUserId(userId);
            emptyReport.setUserName(user.getNomeCompleto());
            emptyReport.setTotalAccumulatedBalance(Duration.ZERO);
            emptyReport.setMonthlySummaries(new ArrayList<>());
            return emptyReport;
        }

        // Declara a variável 'startCalculatingDate' antes de atribuir um valor a ela
        LocalDate startCalculatingDate = oldestRecordOpt.get().getDataHoraRegistro().toLocalDate();
        LocalDate today = LocalDate.now();

        Duration totalAccumulatedBalance = Duration.ZERO;
        List<BankedHoursReportDTO> monthlySummaries = new ArrayList<>();

        // Iterar mês a mês, do primeiro registro até o mês atual
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
            currentMonthIterator = currentMonthIterator.plusMonths(1);
        }

        // Popula e retorna o DTO do relatório acumulado
        BankedHoursAccumulatedReportDTO accumulatedReport = new BankedHoursAccumulatedReportDTO();
        accumulatedReport.setUserId(user.getId());
        accumulatedReport.setUserName(user.getNomeCompleto());
        accumulatedReport.setTotalAccumulatedBalance(totalAccumulatedBalance);
        accumulatedReport.setMonthlySummaries(monthlySummaries);

        return accumulatedReport;
    }

    /**
     * Implementa a lógica de "bater ponto", determinando automaticamente ENTRADA ou SAIDA.
     */
    @Override
    @Transactional // Garante que a operação é atômica (tudo ou nada)
    public RegistrosPonto baterPonto(Integer idUsuario, LocalDateTime dataHoraRegistro) {
        // 1. Verificar se o usuário existe
        userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com ID " + idUsuario + " não encontrado."));

        // 2. Encontrar o último registro de ponto do usuário
        Optional<RegistrosPonto> ultimoRegistroOpt = registroPontoRepository.findTopByIdUsuarioOrderByDataHoraRegistroDesc(idUsuario);

        TipoRegistro proximoTipo;

        if (ultimoRegistroOpt.isEmpty()) {
            // Se não há registros anteriores, o primeiro ponto é sempre ENTRADA
            proximoTipo = TipoRegistro.ENTRADA;
        } else {
            // Se há registros, alternar entre ENTRADA e SAIDA
            RegistrosPonto ultimoRegistro = ultimoRegistroOpt.get();
            if (ultimoRegistro.getTipoRegistro() == TipoRegistro.ENTRADA) {
                proximoTipo = TipoRegistro.SAIDA;
            } else {
                proximoTipo = TipoRegistro.ENTRADA;
            }
        }

        // 3. Criar e salvar o novo registro de ponto
        RegistrosPonto novoPonto = new RegistrosPonto();
        novoPonto.setIdUsuario(idUsuario); // Definimos o idUsuario diretamente
        novoPonto.setDataHoraRegistro(dataHoraRegistro);
        novoPonto.setTipoRegistro(proximoTipo);
        novoPonto.setObservacao("Ponto batido automaticamente pela API."); // Observação padrão
        // createdAt e updatedAt serão preenchidos automaticamente pelas anotações @PrePersist

        return registroPontoRepository.save(novoPonto);
    }

    /**
     * Recupera todos os registros de ponto. (Para uso de administrador)
     */
    @Override
    public List<RegistrosPonto> getAllRegistrosPonto() {
        return registroPontoRepository.findAll();
    }

    /**
     * Recupera um registro de ponto por ID. (Para uso de administrador)
     */
    @Override
    public RegistrosPonto getRegistroPontoById(Integer id) {
        return registroPontoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de ponto com ID " + id + " não encontrado."));
    }

    /**
     * Cria um novo registro de ponto manualmente (por um administrador).
     */
    @Override
    @Transactional
    public RegistrosPonto createRegistroPonto(RegistroPontoAdminDTO registroPontoDTO) {
        // 1. Verificar se o usuário existe antes de criar o registro para ele
        userRepository.findById(registroPontoDTO.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com ID " + registroPontoDTO.getIdUsuario() + " não encontrado."));

        RegistrosPonto novoRegistro = new RegistrosPonto();
        novoRegistro.setIdUsuario(registroPontoDTO.getIdUsuario());
        novoRegistro.setDataHoraRegistro(registroPontoDTO.getDataHoraRegistro());
        novoRegistro.setTipoRegistro(registroPontoDTO.getTipoRegistro()); // Aqui o tipo vem do DTO (admin)
        novoRegistro.setObservacao(registroPontoDTO.getObservacao());

        return registroPontoRepository.save(novoRegistro);
    }

    /**
     * Atualiza um registro de ponto existente (por um administrador).
     */
    @Override
    @Transactional
    public RegistrosPonto updateRegistroPonto(Integer id, RegistroPontoAdminDTO registroPontoDTO) {
        // Encontrar o registro de ponto existente
        RegistrosPonto registroExistente = registroPontoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de ponto com ID " + id + " não encontrado para atualização."));

        // Verificar se o usuário associado (se alterado no DTO) existe
        if (!registroExistente.getIdUsuario().equals(registroPontoDTO.getIdUsuario())) {
            userRepository.findById(registroPontoDTO.getIdUsuario())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Novo Usuário com ID " + registroPontoDTO.getIdUsuario() + " não encontrado para associação."));
            registroExistente.setIdUsuario(registroPontoDTO.getIdUsuario());
        }

        // Atualizar os campos
        registroExistente.setDataHoraRegistro(registroPontoDTO.getDataHoraRegistro());
        registroExistente.setTipoRegistro(registroPontoDTO.getTipoRegistro()); // Tipo pode ser corrigido pelo admin
        registroExistente.setObservacao(registroPontoDTO.getObservacao());
        // updatedAt será preenchido automaticamente pela anotação @PreUpdate

        return registroPontoRepository.save(registroExistente);
    }

    /**
     * Deleta um registro de ponto por ID. (Para uso de administrador)
     */
    @Override
    public void deleteRegistroPonto(Integer id) {
        if (!registroPontoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de ponto com ID " + id + " não encontrado para exclusão.");
        }
        registroPontoRepository.deleteById(id);
    }

    /**
     * Recupera registros de ponto de um usuário em um período específico. (Para uso de administrador/relatório)
     */
    @Override
    public List<RegistrosPonto> getRegistrosPontoByUsuarioAndPeriodo(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim) {
        userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com ID " + idUsuario + " não encontrado."));

        return registroPontoRepository.findByIdUsuarioAndDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(idUsuario, inicio, fim);
    }

    /**
     * Recupera todos os registros de ponto em um período específico. (Para uso de administrador/relatório)
     */
    @Override
    public List<RegistrosPonto> getRegistrosPontoByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return registroPontoRepository.findByDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(inicio, fim);
    }
}