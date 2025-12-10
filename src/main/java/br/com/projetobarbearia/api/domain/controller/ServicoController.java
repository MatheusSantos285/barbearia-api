package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.AtualizarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.CriarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaServicoDTO;
import br.com.projetobarbearia.api.domain.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para gerenciar serviços de barbeiros.
 *
 * <p>Expõe endpoints para criar, listar, consultar, atualizar parcialmente e deletar serviços
 * associados a um barbeiro específico.
 *
 * <p>URLs:
 * <ul>
 *   <li>POST /barbeiros/{barbeiroId}/servicos — cria um novo serviço para o barbeiro.</li>
 *   <li>GET /barbeiros/{barbeiroId}/servicos — lista serviços de um barbeiro.</li>
 *   <li>GET /barbeiros/{barbeiroId}/servicos/{servicoId} — busca um serviço por id.</li>
 *   <li>PATCH /barbeiros/{barbeiroId}/servicos/{servicoId} — atualiza parcialmente um serviço.</li>
 *   <li>DELETE /barbeiros/{barbeiroId}/servicos/{servicoId} — deleta um serviço.</li>
 * </ul>
 */
@RestController
@RequestMapping("/barbeiros/{barbeiroId}/servicos")
public class ServicoController {

    /**
     * Serviço que contém a lógica de negócio para operações com serviços.
     */
    @Autowired
    private ServicoService servicoService;

    /**
     * Cria um novo serviço para o barbeiro informado.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarServicoDTO} no corpo da requisição, validado via {@link Valid}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 201 (Created), cabeçalho Location apontando para o recurso criado
     *       e corpo contendo {@link RespostaServicoDTO} com os dados do serviço criado.</li>
     *   <li>Erros: retorna 400 em caso de validação ou outras respostas conforme regras de negócio.</li>
     * </ul>
     *
     * @param barbeiroId identificador do barbeiro ao qual o serviço será associado.
     * @param dto DTO com os dados do serviço a ser criado.
     * @return ResponseEntity com o serviço criado e o header Location apontando para o recurso.
     */
    @PostMapping
    public ResponseEntity<RespostaServicoDTO> criarServico(
            @PathVariable Long barbeiroId,
            @Valid @RequestBody CriarServicoDTO dto
    ) {
        RespostaServicoDTO servicoCriado = servicoService.criar(barbeiroId, dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{servicoId}")
                .buildAndExpand(barbeiroId, servicoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(servicoCriado);
    }

    /**
     * Lista todos os serviços de um barbeiro.
     *
     * @param barbeiroId identificador do barbeiro cujos serviços serão listados.
     * @return ResponseEntity com a lista de {@link RespostaServicoDTO} e status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<RespostaServicoDTO>> listarServicosDoBarbeiro(@PathVariable Long barbeiroId) {
        List<RespostaServicoDTO> servicos = servicoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok(servicos);
    }

    /**
     * Busca um serviço pelo seu identificador.
     *
     * @param servicoId identificador do serviço a ser retornado.
     * @return ResponseEntity com o {@link RespostaServicoDTO} encontrado e status 200 (OK).
     */
    @GetMapping("/{servicoId}")
    public ResponseEntity<RespostaServicoDTO> buscarServicoPorId(@PathVariable Long servicoId) {
        RespostaServicoDTO servico = servicoService.buscarPorId(servicoId);
        return ResponseEntity.ok(servico);
    }

    /**
     * Atualiza parcialmente um serviço existente.
     *
     * <p>Entrada: {@code servicoId} como {@link PathVariable} e {@link AtualizarServicoDTO} no corpo.
     *
     * @param servicoId identificador do serviço a ser atualizado.
     * @param dto DTO contendo os campos a serem atualizados.
     * @return ResponseEntity com o {@link RespostaServicoDTO} atualizado e status 200 (OK).
     */
    @PatchMapping("/{servicoId}")
    public ResponseEntity<RespostaServicoDTO> atualizarParcialmenteServico(@PathVariable Long servicoId, @Valid @RequestBody AtualizarServicoDTO dto) {
        RespostaServicoDTO servicoAtualizado = servicoService.atualizar(servicoId, dto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    /**
     * Deleta um serviço pelo seu identificador.
     *
     * @param servicoId identificador do serviço a ser deletado.
     * @return ResponseEntity com status 204 (No Content) quando a exclusão for bem-sucedida.
     */
    @DeleteMapping("/{servicoId}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long servicoId) {
        servicoService.deletar(servicoId);
        return ResponseEntity.noContent().build();
    }
}
