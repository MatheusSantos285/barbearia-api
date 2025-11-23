package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CriarAgendamentoDTO(
        @NotNull
        Long barbeiroId,
        @NotNull
        Long clienteId,
        @NotNull
        Long servicoId,
        @NotNull
        @Future
        LocalDateTime dataHoraInicio
) {
}
