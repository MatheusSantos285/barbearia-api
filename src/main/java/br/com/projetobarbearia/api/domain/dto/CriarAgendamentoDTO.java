package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CriarAgendamentoDTO(
        @NotEmpty
        Long barbeiroId,
        @NotEmpty
        Long clienteId,
        @NotEmpty
        Long servicoId,

        @NotNull
        @Future
        LocalDateTime datahoraInicio
) {
}
