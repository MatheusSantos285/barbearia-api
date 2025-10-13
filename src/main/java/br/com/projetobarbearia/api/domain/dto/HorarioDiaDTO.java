package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;

public record HorarioDiaDTO(
        @NotNull
        @Min(0) @Max(6)
        Integer diaSemana,
        @NotNull
        LocalTime horaInicio,
        @NotNull
        LocalTime horaFim
) {
}
