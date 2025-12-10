package br.com.projetobarbearia.api.domain.controller.exception;

import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.exception.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Tratador centralizado de exceções para controladores REST.
 *
 * <p>Esta classe captura exceções lançadas pelos controllers e as converte em
 * respostas HTTP apropriadas com códigos de status e mensagens legíveis.
 *
 * <p>Segue o padrão do Spring Boot de usar {@link ControllerAdvice} para aplicar
 * tratamento global de exceções.</p>
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Trata exceções do tipo {@link EntidadeNaoEncontradaException}.
     *
     * <p>Quando uma entidade requisitada não é encontrada, retorna uma resposta
     * com HTTP 404 (NOT_FOUND) e o corpo contendo a mensagem da exceção.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: exceção {@link EntidadeNaoEncontradaException} lançada pela camada de serviço.</li>
     *   <li>Saída: {@link ResponseEntity} com status 404 e corpo texto contendo a mensagem de erro.</li>
     *   <li>Erros: não há erros adicionais esperados neste handler.</li>
     * </ul>
     *
     * @param ex exceção indicando que a entidade não foi encontrada.
     * @return ResponseEntity com status 404 e mensagem da exceção no corpo.
     */
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<String> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Trata exceções do tipo {@link RegraDeNegocioException}.
     *
     * <p>Quando ocorre uma regra de negócio inválida, retorna uma resposta
     * com HTTP 400 (BAD_REQUEST) e o corpo contendo a mensagem da exceção.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: exceção {@link RegraDeNegocioException} lançada pela camada de serviço.</li>
     *   <li>Saída: {@link ResponseEntity} com status 400 e corpo texto contendo a mensagem de erro.</li>
     * </ul>
     *
     * @param ex exceção representando violação de regra de negócio.
     * @return ResponseEntity com status 400 e mensagem da exceção no corpo.
     */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<String> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
