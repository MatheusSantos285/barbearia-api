package br.com.projetobarbearia.api.domain.dto;

import java.math.BigDecimal;

public record RespostaServicoDTO(
        Long id,
        String nomeServico,
        Integer duracaoMinutos,
        BigDecimal preco,
        Long barbeiroId
) {}
