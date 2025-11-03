package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarClienteDTO(
        @Size(min = 3, max = 100)
        String nome,

        @Size(min = 10, max = 15)
        String telefone
) {}
