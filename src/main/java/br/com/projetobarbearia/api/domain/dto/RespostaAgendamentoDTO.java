package br.com.projetobarbearia.api.domain.dto;

import br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus;

import java.time.LocalDateTime;

public record RespostaAgendamentoDTO(
        Long id,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        AgendamentoStatus status,
        Long barbeiroId,
        String barbeiroNome,
        Long clienteId,
        String clienteNome,
        Long servicoId,
        String servicoNome,
        String servicoPreco
) {}
