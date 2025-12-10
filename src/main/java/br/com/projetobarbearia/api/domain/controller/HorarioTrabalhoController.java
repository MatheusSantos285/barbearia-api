package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.DefinirHorariosDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaHorarioTrabalhoDTO;
import br.com.projetobarbearia.api.domain.service.HorarioTrabalhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST responsável por gerenciar os horários de trabalho de barbeiros.
 *
 * <p>Expõe endpoints para definir (substituir) os horários de trabalho de um barbeiro
 * e para listar os horários já cadastrados para um barbeiro específico.
 *
 * <p>URLs:
 * <ul>
 *   <li>PUT /barbeiros/{barbeiroId}/horarios-trabalho — define os horários de trabalho para o barbeiro.</li>
 *   <li>GET /barbeiros/{barbeiroId}/horarios-trabalho — lista os horários de trabalho do barbeiro.</li>
 * </ul>
 */
@RestController
@RequestMapping("/barbeiros/{barbeiroId}/horarios-trabalho")
public class HorarioTrabalhoController {

    /**
     * Serviço que encapsula a lógica de negócio relacionada a horários de trabalho.
     */
    @Autowired
    private HorarioTrabalhoService horarioTrabalhoService;

    /**
     * Define (substitui) os horários de trabalho de um barbeiro.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link DefinirHorariosDTO} no corpo da requisição, validado via {@link Valid}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) contendo a lista de {@link RespostaHorarioTrabalhoDTO}
     *       representando os horários gravados.</li>
     *   <li>Erros: retorna 400 em caso de validação ou outras respostas conforme regras de negócio.</li>
     * </ul>
     *
     * @param barbeiroId identificador do barbeiro cujos horários serão definidos.
     * @param dto DTO contendo a definição dos horários a serem aplicados.
     * @return ResponseEntity com a lista de horários de trabalho salvos para o barbeiro.
     */
    @PutMapping
    public ResponseEntity<List<RespostaHorarioTrabalhoDTO>> definirHorarios(
            @PathVariable Long barbeiroId,
            @Valid @RequestBody DefinirHorariosDTO dto
    ) {
        List<RespostaHorarioTrabalhoDTO> horariosSalvos = horarioTrabalhoService.definirHorarios(barbeiroId, dto);
        return ResponseEntity.ok(horariosSalvos);
    }

    /**
     * Lista os horários de trabalho configurados para o barbeiro informado.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code barbeiroId} como {@link PathVariable}.</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) contendo uma lista de {@link RespostaHorarioTrabalhoDTO}.</li>
     * </ul>
     *
     * @param barbeiroId identificador do barbeiro cujos horários serão retornados.
     * @return ResponseEntity com a lista de horários do barbeiro.
     */
    @GetMapping
    public ResponseEntity<List<RespostaHorarioTrabalhoDTO>> listarHorarios(@PathVariable Long barbeiroId) {
        List<RespostaHorarioTrabalhoDTO> horarios = horarioTrabalhoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok(horarios);
    }
}
