package br.com.projetobarbearia.api.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualizar as informações de um barbeiro.
 *
 * @param nome     Nome do barbeiro.
 * @param email    Email do barbeiro.
 * @param senha    Senha do barbeiro.
 * @param telefone Telefone do barbeiro.
 */
public record AtualizarBarbeiroDTO(
        @Size(min = 3, max = 100)
        String nome,

        @Email
        String email,

        @Size(min = 6)
        String senha,

        String telefone
) {}
