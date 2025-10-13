package br.com.projetobarbearia.api.domain.dto;

import java.time.LocalTime;

public record RespostaHorarioTrabalhoDTO(
        Long id,
        Integer diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        Long barbeiroId
) {}
