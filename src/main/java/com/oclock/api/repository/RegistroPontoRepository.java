package com.oclock.api.repository; // Ajuste o pacote

import com.oclock.api.model.RegistrosPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroPontoRepository extends JpaRepository<RegistrosPonto, Integer> {

    List<RegistrosPonto> findByIdUsuarioAndDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(
            Integer idUsuario, LocalDateTime dataInicio, LocalDateTime dataFim);

    List<RegistrosPonto> findByDataHoraRegistroBetweenOrderByDataHoraRegistroAsc(
            LocalDateTime dataInicio, LocalDateTime dataFim);

    Optional<RegistrosPonto> findTopByIdUsuarioOrderByDataHoraRegistroDesc(Integer idUsuario);
}