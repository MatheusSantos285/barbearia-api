package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarClienteDTO(
        @NotBlank
        @Size(min = 3, max = 100)
        String nome,

        @NotBlank
        @Size(min = 10, max = 15)
        String telefone,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha
) {}
