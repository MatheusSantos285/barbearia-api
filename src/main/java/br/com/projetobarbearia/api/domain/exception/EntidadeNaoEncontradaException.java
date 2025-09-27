package br.com.projetobarbearia.api.domain.exception;

/**
 * Exceção a ser lançada quando uma entidade não for encontrada no banco de dados.
 * A anotação @ResponseStatus(HttpStatus.NOT_FOUND) instrui o Spring a retornar
 * automaticamente um código de status HTTP 404 quando esta exceção não for tratada.
 */
public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}