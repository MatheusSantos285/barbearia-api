package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarServicoDTO(
        @NotBlank
        String nomeServico,
        @NotNull
        @Positive
        Integer duracaoMinutos,
        @NotNull
        @Positive
        BigDecimal preco
) {
}
