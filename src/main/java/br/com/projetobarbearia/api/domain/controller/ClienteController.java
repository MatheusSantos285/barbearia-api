package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.AtualizarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.CriarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaClienteDTO;
import br.com.projetobarbearia.api.domain.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para gerenciar clientes.
 *
 * <p>Expõe endpoints para cadastro, atualização parcial, listagem, busca por identificador
 * ou telefone, e exclusão de clientes.
 *
 * <p>URLs principais:
 * <ul>
 *   <li>POST /clientes — cria um novo cliente.</li>
 *   <li>PATCH /clientes/{id} — atualiza parcialmente um cliente existente.</li>
 *   <li>GET /clientes — lista todos os clientes.</li>
 *   <li>GET /clientes/{id} — busca um cliente por ID.</li>
 *   <li>GET /clientes/telefone/{telefone} — busca um cliente por telefone.</li>
 *   <li>DELETE /clientes/{id} — deleta um cliente por ID.</li>
 * </ul>
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    /**
     * Serviço que encapsula a lógica de negócio para operações relacionadas a clientes.
     */
    @Autowired
    private ClienteService clienteService;

    /**
     * Cria um novo cliente.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarClienteDTO} no corpo da requisição. Validação aplicada via {@link Valid}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 201 (Created), cabeçalho Location apontando para o recurso criado
     *       e corpo contendo {@link RespostaClienteDTO} com os dados do cliente criado.</li>
     *   <li>Erros: retorna 400 em caso de validação ou outras respostas conforme regras de negócio do serviço.</li>
     * </ul>
     *
     * @param dto dados do cliente a serem persistidos.
     * @return ResponseEntity com o cliente criado e o header Location apontando para o recurso.
     */
    @PostMapping
    public ResponseEntity<RespostaClienteDTO> criarCliente(@Valid @RequestBody CriarClienteDTO dto) {
        RespostaClienteDTO clienteCriado = clienteService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(clienteCriado);
    }

    /**
     * Atualiza parcialmente os dados de um cliente existente.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code id} do cliente como {@link PathVariable} e {@link AtualizarClienteDTO} no corpo.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e {@link RespostaClienteDTO} com os dados atualizados.</li>
     *   <li>Erros: retorna 404 se o cliente não for encontrado ou 400/409 conforme regras de negócio.</li>
     * </ul>
     *
     * @param id identificador do cliente a ser atualizado.
     * @param dto campos que serão atualizados no cliente.
     * @return ResponseEntity com o cliente atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RespostaClienteDTO> atualizarParcialmenteCliente(@PathVariable Long id, @Valid @RequestBody AtualizarClienteDTO dto) {
        RespostaClienteDTO clienteAtualizado = clienteService.atualizarParcialmente(id, dto);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * Retorna a lista de todos os clientes cadastrados.
     *
     * @return ResponseEntity contendo uma lista de {@link RespostaClienteDTO} com status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<RespostaClienteDTO>> listarTodosClientes() {
        List<RespostaClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca um cliente pelo seu ID.
     *
     * @param id identificador do cliente a ser buscado.
     * @return ResponseEntity com o cliente encontrado e status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<RespostaClienteDTO> buscarClientePorId(@PathVariable Long id) {
        RespostaClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Busca um cliente pelo seu telefone.
     *
     * <p>Útil para fluxos onde o telefone é um identificador alternativo ao ID.
     *
     * @param telefone número de telefone do cliente como {@link PathVariable}.
     * @return ResponseEntity com o cliente encontrado e status 200 (OK).
     */
    @GetMapping("/telefone/{telefone}")
    public ResponseEntity<RespostaClienteDTO> buscarClientePorTelefone(@PathVariable String telefone) {
        RespostaClienteDTO cliente = clienteService.buscarPorTelefone(telefone);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Remove um cliente identificado pelo ID.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code id} do cliente como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 204 (No Content) quando a exclusão for bem-sucedida.</li>
     *   <li>Erros: retorna 404 se o cliente não existir.</li>
     * </ul>
     *
     * @param id identificador do cliente a ser removido.
     * @return ResponseEntity sem corpo com status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
