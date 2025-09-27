package br.com.projetobarbearia.api.domain.dto;

/**
 * DTO para responder com as informações de um barbeiro.
 *
 * @param id       ID do barbeiro.
 * @param nome     Nome do barbeiro.
 * @param email    Email do barbeiro.
 * @param telefone Telefone do barbeiro.
 */
public record RespostaBarbeiroDTO(
        Long id,
        String nome,
        String email,
        String telefone
) {
}
