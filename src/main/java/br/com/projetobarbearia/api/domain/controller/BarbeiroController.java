package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.AtualizarBarbeiroDTO;
import br.com.projetobarbearia.api.domain.dto.CriarBarbeiroDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaBarbeiroDTO;
import br.com.projetobarbearia.api.domain.service.BarbeiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para gerenciar as operações relacionadas a barbeiros.
 *
 * <p>Responsável por expor endpoints para cadastro, atualização parcial, listagem,
 * consulta por id e remoção de barbeiros.
 *
 * <p>URLs:
 * <ul>
 *   <li>POST /barbeiros — cria um barbeiro.</li>
 *   <li>PATCH /barbeiros/{id} — atualiza parcialmente um barbeiro.</li>
 *   <li>GET /barbeiros — lista todos os barbeiros.</li>
 *   <li>GET /barbeiros/{id} — busca barbeiro por id.</li>
 *   <li>DELETE /barbeiros/{id} — deleta barbeiro por id.</li>
 * </ul>
 */
@RestController
@RequestMapping("/barbeiros")
public class BarbeiroController {

    /**
     * Serviço que contém a lógica de negócio para operações com barbeiros.
     */
    @Autowired
    private BarbeiroService barbeiroService;

    /**
     * Cria um novo barbeiro.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarBarbeiroDTO} com os dados do barbeiro (validação aplicada via {@link Valid}).</li>
     *   <li>Saída: {@link ResponseEntity} com status 201 (Created), cabeçalho Location apontando para o recurso criado
     *       e corpo contendo {@link RespostaBarbeiroDTO}.</li>
     *   <li>Erros: retorna 400 em caso de validação ou outras respostas conforme regras de negócio.</li>
     * </ul>
     *
     * @param dto DTO com os dados para a criação do barbeiro.
     * @return ResponseEntity com o barbeiro criado e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<RespostaBarbeiroDTO> criarBarbeiro(@Valid @RequestBody CriarBarbeiroDTO dto) {

        RespostaBarbeiroDTO barbeiroCriado = barbeiroService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(barbeiroCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(barbeiroCriado);
    }

    /**
     * Atualiza parcialmente um barbeiro existente.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code id} do barbeiro como {@link PathVariable} e {@link AtualizarBarbeiroDTO} no corpo.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e {@link RespostaBarbeiroDTO} atualizado.</li>
     *   <li>Erros: retorna 404 se o barbeiro não for encontrado ou 400/409 conforme regras de negócio.</li>
     * </ul>
     *
     * @param id O ID do barbeiro a ser atualizado.
     * @param dto DTO com os dados a serem atualizados.
     * @return ResponseEntity com o barbeiro atualizado e o status 200 (OK).
     */
    @PatchMapping("/{id}")// Mapeia este método para requisições HTTP PATCH.
    public ResponseEntity<RespostaBarbeiroDTO> atualizarParcialmenteBarbeiro(@PathVariable Long id, @Valid @RequestBody AtualizarBarbeiroDTO dto) {
        RespostaBarbeiroDTO barbeiroAtualizado = barbeiroService.atualizar(id, dto);
        return ResponseEntity.ok(barbeiroAtualizado);
    }

    /**
     * Lista todos os barbeiros cadastrados.
     *
     * @return ResponseEntity com a lista de barbeiros e o status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<RespostaBarbeiroDTO>> listarTodosBarbeiros() {
        List<RespostaBarbeiroDTO> barbeiros = barbeiroService.listarTodos();
        return ResponseEntity.ok(barbeiros);
    }

    /**
     * Busca um barbeiro pelo seu ID.
     *
     * @param id O ID do barbeiro a ser buscado.
     * @return ResponseEntity com o barbeiro encontrado e o status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<RespostaBarbeiroDTO> buscarBarbeiroPorId(@PathVariable Long id) {
        RespostaBarbeiroDTO barbeiro = barbeiroService.buscarPorId(id);
        return ResponseEntity.ok(barbeiro);
    }

    /**
     * Deleta um barbeiro pelo seu ID.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code id} do barbeiro como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 204 (No Content) quando a exclusão for bem-sucedida.</li>
     *   <li>Erros: retorna 404 se o barbeiro não existir.</li>
     * </ul>
     *
     * @param id O ID do barbeiro a ser deletado.
     * @return ResponseEntity com o status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarBarbeiro(@PathVariable Long id) {
        barbeiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
