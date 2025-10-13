package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AtualizarServicoDTO(
        String nomeServico,
        @Positive
        Integer duracaoMinutos,
        @Positive
        BigDecimal preco
) {
}
