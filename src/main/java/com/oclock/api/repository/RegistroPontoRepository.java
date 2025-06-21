package com.oclock.api.repository; // Ajuste o pacote

import com.oclock.api.model.RegistrosPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroPontoRepository extends JpaRepository<RegistrosPonto, Integer> {

    // Buscar todos os registros de um usuário em um período, ordenados por data e hora
    List<RegistrosPonto> findByIdUsuarioAndDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(
            Integer idUsuario, LocalDateTime dataInicio, LocalDateTime dataFim);

    // Buscar todos os registros em um período, para todos os usuários (útil para admins)
    List<RegistrosPonto> findByDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(
            LocalDateTime dataInicio, LocalDateTime dataFim);

    // Encontrar o último registro de ponto para um usuário (útil para inferir se é entrada ou saída)
    Optional<RegistrosPonto> findTopByIdUsuarioOrderByDataHoraRegistroDesc(Integer idUsuario);
}