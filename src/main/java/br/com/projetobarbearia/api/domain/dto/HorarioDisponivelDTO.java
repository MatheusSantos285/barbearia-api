package br.com.projetobarbearia.api.domain.dto;

import java.time.LocalTime;

public record HorarioDisponivelDTO(
        LocalTime horario
) {
}
