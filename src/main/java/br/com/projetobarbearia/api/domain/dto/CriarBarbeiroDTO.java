package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criar um novo barbeiro.
 *
 * @param nome     Nome do barbeiro.
 * @param email    Email do barbeiro.
 * @param senha    Senha do barbeiro.
 * @param telefone Telefone do barbeiro.
 */
public record CriarBarbeiroDTO(
        @NotBlank
        @Size(min = 3, max = 100)
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String senha,

        @NotBlank
        String telefone
){}
