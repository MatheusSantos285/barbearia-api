package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.CriarAgendamentoDTO;
import br.com.projetobarbearia.api.domain.dto.HorarioDisponivelDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaAgendamentoDTO;
import br.com.projetobarbearia.api.domain.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gerenciar agendamentos.
 *
 * <p>Expõe endpoints para criar, cancelar e listar agendamentos, assim como consultar
 * horários disponíveis para um barbeiro e serviço em uma data específica.
 *
 * <p>Endpoints principais:
 * <ul>
 *   <li>POST /agendamentos — cria um novo agendamento.</li>
 *   <li>PATCH /agendamentos/{id}/cancelar — cancela um agendamento existente.</li>
 *   <li>GET /agendamentos/barbeiro/{barbeiroId} — lista agendamentos de um barbeiro.</li>
 *   <li>GET /agendamentos/cliente/{clienteId} — lista agendamentos de um cliente.</li>
 *   <li>GET /agendamentos/disponibilidade — lista horários disponíveis para um barbeiro e serviço em uma data.</li>
 * </ul>
 */
@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    /**
     * Serviço que contém a lógica de negócio relacionada a agendamentos.
     */
    @Autowired
    private AgendamentoService agendamentoService;

    /**
     * Cria um novo agendamento.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: JSON representando {@link CriarAgendamentoDTO}. Campos são validados com {@link Valid}.</li>
     *   <li>Saída: {@link ResponseEntity} com status HTTP 201 (Created), cabeçalho Location apontando para o recurso criado
     *       e corpo contendo {@link RespostaAgendamentoDTO} com os dados do agendamento.</li>
     *   <li>Erros: retorna 400 em caso de validação, ou outras respostas conforme regras de negócio lançadas pelo serviço.</li>
     * </ul>
     *
     * @param dto dados do agendamento a ser criado.
     * @return ResponseEntity contendo o recurso criado e o URI no header Location.
     */
    @PostMapping
    public ResponseEntity<RespostaAgendamentoDTO> agendar(@Valid @RequestBody CriarAgendamentoDTO dto) {
        RespostaAgendamentoDTO  agendamentoCriado = agendamentoService.agendar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agendamentoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(agendamentoCriado);
    }

    /**
     * Cancela um agendamento existente identificado pelo ID.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code id} do agendamento como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e o {@link RespostaAgendamentoDTO} atualizado.</li>
     *   <li>Erros: retorna 404 se o agendamento não existir ou 400/409 conforme regras de negócio aplicáveis.</li>
     * </ul>
     *
     * @param id identificador do agendamento a ser cancelado.
     * @return ResponseEntity com o agendamento cancelado.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<RespostaAgendamentoDTO> cancelarAgendamento(@PathVariable Long id) {
        RespostaAgendamentoDTO agendamentoCancelado = agendamentoService.cancelar(id);
        return ResponseEntity.ok(agendamentoCancelado);
    }

    /**
     * Lista os agendamentos de um barbeiro identificado por {@code barbeiroId}.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code barbeiroId} como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e uma lista de {@link RespostaAgendamentoDTO}.</li>
     * </ul>
     *
     * @param barbeiroId id do barbeiro cujos agendamentos serão retornados.
     * @return ResponseEntity contendo a lista de agendamentos do barbeiro.
     */
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<RespostaAgendamentoDTO>> listarAgendamentosDoBarbeiro(@PathVariable Long barbeiroId) {
        List<RespostaAgendamentoDTO> agendamentos = agendamentoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok().body(agendamentos);
    }

    /**
     * Lista os agendamentos de um cliente identificado por {@code clienteId}.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code clienteId} como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e uma lista de {@link RespostaAgendamentoDTO}.</li>
     * </ul>
     *
     * @param clienteId id do cliente cujos agendamentos serão retornados.
     * @return ResponseEntity contendo a lista de agendamentos do cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<RespostaAgendamentoDTO>> listarAgendamentosDoCliente(@PathVariable Long clienteId) {
        List<RespostaAgendamentoDTO> agendamentos = agendamentoService.listarPorCliente(clienteId);
        return ResponseEntity.ok().body(agendamentos);
    }

    /**
     * Lista horários disponíveis para um barbeiro e serviço em uma data específica.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: parâmetros de consulta {@code barbeiroId}, {@code servicoId} e {@code data}.
     *       O parâmetro {@code data} é mapeado para {@link LocalDate}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) contendo uma lista de {@link HorarioDisponivelDTO}.</li>
     *   <li>Erros: retorna 400 em caso de parâmetros inválidos ou conforme regras de negócio.</li>
     * </ul>
     *
     * @param barbeiroId identificador do barbeiro.
     * @param servicoId identificador do serviço.
     * @param data data desejada para verificar disponibilidade.
     * @return ResponseEntity contendo a lista de horários disponíveis.
     */
    @GetMapping("/disponibilidade")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam Long servicoId,
            @RequestParam LocalDate data
    ) {
        List<HorarioDisponivelDTO> horarios = agendamentoService.listarHorariosDisponiveis(barbeiroId, servicoId, data);
        return ResponseEntity.ok(horarios);
    }
}
