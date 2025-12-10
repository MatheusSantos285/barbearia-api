package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.DefinirHorariosDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaHorarioTrabalhoDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.model.HorarioTrabalho;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.HorarioTrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço que gerencia os horários de trabalho dos barbeiros.
 *
 * <p>Responsabilidades principais:
 * <ul>
 *   <li>Definir (substituir) os horários de trabalho de um barbeiro.</li>
 *   <li>Listar os horários configurados para um barbeiro.</li>
 * </ul>
 *
 * <p>Lançamentos de exceção:
 * <ul>
 *   <li>{@link EntidadeNaoEncontradaException} quando o barbeiro referido não existir.</li>
 * </ul>
 */
@Service
public class HorarioTrabalhoService {

    /**
     * Repositório de horários de trabalho para persistência e consulta.
     */
    @Autowired
    private HorarioTrabalhoRepository horarioTrabalhoRepository;

    /**
     * Repositório de barbeiros usado para validar a existência do barbeiro alvo.
     */
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    /**
     * Substitui os horários de trabalho de um barbeiro pelos horários fornecidos no DTO.
     *
     * <p>Fluxo:
     * <ol>
     *   <li>Valida a existência do barbeiro.</li>
     *   <li>Remove os horários antigos do barbeiro.</li>
     *   <li>Cria e persiste os novos horários a partir do {@link DefinirHorariosDTO}.</li>
     *   <li>Retorna uma lista de {@link RespostaHorarioTrabalhoDTO} representando os horários salvos.</li>
     * </ol>
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code barbeiroId} e {@link DefinirHorariosDTO} contendo a lista de horários a definir.</li>
     *   <li>Saída: lista de {@link RespostaHorarioTrabalhoDTO} com os horários persistidos.</li>
     *   <li>Erros: {@link EntidadeNaoEncontradaException} se o barbeiro não existir.</li>
     * </ul>
     *
     * @param barbeiroId id do barbeiro cujos horários serão substituídos.
     * @param dto DTO contendo os horários a serem definidos.
     * @return lista de DTOs representando os horários salvos.
     */
    @Transactional
    public List<RespostaHorarioTrabalhoDTO> definirHorarios(Long barbeiroId, DefinirHorariosDTO dto) {
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<HorarioTrabalho> horariosAntigos = horarioTrabalhoRepository.findByBarbeiroId(barbeiroId);
        horarioTrabalhoRepository.deleteAll(horariosAntigos);

        List<HorarioTrabalho> novosHorarios = dto.horarios().stream().map(horarioDia -> {
            HorarioTrabalho novoHorario = new HorarioTrabalho();
            novoHorario.setBarbeiro(barbeiro);
            novoHorario.setDiaSemana(horarioDia.getDiaSemana());
            novoHorario.setHoraInicio(horarioDia.getHoraInicio());
            novoHorario.setHoraFim(horarioDia.getHoraFim());
            return novoHorario;
        }).collect(Collectors.toList());
        
        List<HorarioTrabalho> horariosSalvos = horarioTrabalhoRepository.saveAll(novosHorarios);
        
        return horariosSalvos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna a lista de horários de trabalho configurados para o barbeiro informado.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@code barbeiroId}.</li>
     *   <li>Saída: lista de {@link RespostaHorarioTrabalhoDTO}.</li>
     *   <li>Erros: {@link EntidadeNaoEncontradaException} se o barbeiro não existir.</li>
     * </ul>
     *
     * @param barbeiroId id do barbeiro.
     * @return lista de DTOs representando os horários do barbeiro.
     */
    public List<RespostaHorarioTrabalhoDTO> listarPorBarbeiro(Long barbeiroId) {
        if (!barbeiroRepository.existsById(barbeiroId)) {
            throw new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId);
        }

        return horarioTrabalhoRepository.findByBarbeiroId(barbeiroId).stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte a entidade {@link HorarioTrabalho} para o DTO de resposta.
     *
     * @param horarioTrabalho entidade a ser convertida.
     * @return {@link RespostaHorarioTrabalhoDTO} com os campos expostos pela API.
     */
    private RespostaHorarioTrabalhoDTO converterParaRespostaDTO(HorarioTrabalho horarioTrabalho) {
        return new RespostaHorarioTrabalhoDTO(
                horarioTrabalho.getId(),
                horarioTrabalho.getDiaSemana(),
                horarioTrabalho.getHoraInicio(),
                horarioTrabalho.getHoraFim(),
                horarioTrabalho.getBarbeiro().getId()
        );
    }

}
