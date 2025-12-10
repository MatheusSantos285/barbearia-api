package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.CriarAgendamentoDTO;
import br.com.projetobarbearia.api.domain.dto.HorarioDisponivelDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaAgendamentoDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.exception.RegraDeNegocioException;
import br.com.projetobarbearia.api.domain.model.*;
import br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus;
import br.com.projetobarbearia.api.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo processo de agendamento de serviços na barbearia.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Criar agendamentos garantindo consistência (verifica barbeiro, cliente, serviço e conflitos).</li>
 *   <li>Cancelar agendamentos respeitando regras de negócio (ex.: não cancelar após a data).</li>
 *   <li>Listar agendamentos por barbeiro ou cliente.</li>
 *   <li>Calcular horários disponíveis para um barbeiro e serviço em uma data.</li>
 * </ul>
 *
 * <p>Exceções lançadas:
 * <ul>
 *   <li>{@link EntidadeNaoEncontradaException} quando entidades referenciadas não existem.</li>
 *   <li>{@link RegraDeNegocioException} para violação de regras do domínio (por exemplo, conflito de horário).</li>
 * </ul>
 */
@Service
public class AgendamentoService {

    /**
     * Repositório de agendamentos para persistência e consultas customizadas.
     */
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    /**
     * Repositório de barbeiros (usado para validações de existência).</li>
     */
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    /**
     * Repositório de clientes (usado para validações de existência).
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Repositório de serviços (usado para validar vínculo serviços/barbeiro e duração).
     */
    @Autowired
    private ServicoRepository servicoRepository;

    /**
     * Repositório de horários de trabalho dos barbeiros.
     */
    @Autowired
    private HorarioTrabalhoRepository  horarioTrabalhoRepository;

    /**
     * Cria um novo agendamento baseado nos dados do DTO.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarAgendamentoDTO} com ids de barbeiro, cliente, serviço e data/hora de início.</li>
     *   <li>Saída: {@link RespostaAgendamentoDTO} representando o agendamento persistido.</li>
     *   <li>Erros: {@link EntidadeNaoEncontradaException} se barbeiro/cliente/serviço não existirem;
     *       {@link RegraDeNegocioException} em caso de conflito de horário ou serviço incompatível.</li>
     * </ul>
     *
     * @param dto dados para criação do agendamento.
     * @return dados do agendamento criado.
     */
    @Transactional
    public RespostaAgendamentoDTO agendar(CriarAgendamentoDTO dto) {

        Barbeiro barbeiro = barbeiroRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + dto.barbeiroId()));
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + dto.clienteId()));
        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + dto.servicoId()));

        if (!servico.getBarbeiro().getId().equals(barbeiro.getId())) {
            throw new RegraDeNegocioException("O serviço informado não pertence ao barbeiro selecionado.");
        }

        LocalDateTime dataHoraInicio = dto.dataHoraInicio();
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(servico.getDuracaoMinutos());

        agendamentoRepository.findConflictingAgendamento(
                barbeiro.getId(), dataHoraInicio, dataHoraFim
        ).ifPresent(conflito -> {
            throw new RegraDeNegocioException("Horário indisponível. Já existe um agendamento para este período.");
        });

        Agendamento agendamento = new Agendamento();
        agendamento.setBarbeiro(barbeiro);
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(dataHoraInicio);
        agendamento.setDataHoraFim(dataHoraFim);
        agendamento.setStatus(AgendamentoStatus.MARCADO);

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return converterParaRespostaDTO(agendamentoSalvo);
    }

    /**
     * Cancela um agendamento existente se cumprir as regras de negócio.
     *
     * <p>Regras aplicadas:
     * <ul>
     *   <li>Não é possível cancelar um agendamento que já ocorreu.</li>
     *   <li>Apenas agendamentos com status {@link AgendamentoStatus#MARCADO} podem ser cancelados.</li>
     * </ul>
     *
     * @param agendamentoId id do agendamento a ser cancelado.
     * @return DTO com os dados do agendamento após o cancelamento.
     * @throws EntidadeNaoEncontradaException se o agendamento não existir.
     * @throws RegraDeNegocioException se a regra de cancelamento for violada.
     */
    @Transactional
    public RespostaAgendamentoDTO cancelar(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Agendamento não encontrado: " + agendamentoId));

        if (agendamento.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new RegraDeNegocioException("Não é possível cancelar um agendamento que já ocorreu.");
        }
        if (agendamento.getStatus() != AgendamentoStatus.MARCADO) {
            throw new RegraDeNegocioException("Apenas agendamentos com status 'MARCADO' podem ser cancelados.");
        }

        agendamento.setStatus(AgendamentoStatus.CANCELADO_PELO_CLIENTE);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return converterParaRespostaDTO(agendamentoSalvo);
    }

    /**
     * Lista todos os agendamentos de um barbeiro.
     *
     * @param barbeiroId id do barbeiro.
     * @return lista de {@link RespostaAgendamentoDTO} associados ao barbeiro.
     * @throws EntidadeNaoEncontradaException se o barbeiro não existir.
     */
    public List<RespostaAgendamentoDTO> listarPorBarbeiro(Long barbeiroId) {
        barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<Agendamento> agendamentos = agendamentoRepository.findByBarbeiroId(barbeiroId);

        return agendamentos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os agendamentos de um cliente.
     *
     * @param clienteId id do cliente.
     * @return lista de {@link RespostaAgendamentoDTO} associados ao cliente.
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     */
    public List<RespostaAgendamentoDTO> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + clienteId));

        List<Agendamento> agendamentos = agendamentoRepository.findByClienteId(clienteId);

        return agendamentos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcula os horários disponíveis para um barbeiro e um serviço em uma data específica.
     *
     * <p>Fluxo resumido:
     * <ol>
     *   <li>Valida existência do serviço e obtém sua duração.</li>
     *   <li>Encontra o horário de trabalho do barbeiro para o dia da semana solicitado.</li>
     *   <li>Coleta agendamentos já existentes do dia (ignorando os cancelados).</li>
     *   <li>Percorre os slots do expediente (com intervalo fixo de 30 minutos) e agrega os slots
     *       que não conflitam com agendamentos existentes.</li>
     * </ol>
     *
     * @param barbeiroId id do barbeiro.
     * @param servicoId id do serviço.
     * @param data data para a qual calcular a disponibilidade.
     * @return lista de {@link HorarioDisponivelDTO} representando horários iniciáveis disponíveis.
     * @throws EntidadeNaoEncontradaException se o serviço não existir.
     * @throws RegraDeNegocioException se o barbeiro não trabalhar no dia solicitado.
     */
    public List<HorarioDisponivelDTO> listarHorariosDisponiveis (Long barbeiroId, Long servicoId, LocalDate data) {
        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado."));
        int duracaoServico =  servico.getDuracaoMinutos();

        int diaSemanaJava = data.getDayOfWeek().getValue();
        int diaSemanaBanco = (diaSemanaJava == 7) ? 0 : diaSemanaJava;

        HorarioTrabalho horarioTrabalho = horarioTrabalhoRepository.findByBarbeiroId(barbeiroId).stream()
                .filter(h -> h.getDiaSemana() == diaSemanaBanco)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("O barbeiro não trabalha nesta data (" + data.getDayOfWeek() + ")"));

        List<AgendamentoStatus> statusExcluidos = List.of(
                AgendamentoStatus.CANCELADO_PELO_CLIENTE,
                AgendamentoStatus.CANCELADO_PELO_BARBEIRO
        );

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(LocalTime.MAX);

        List<Agendamento> agendamentosExistentes = agendamentoRepository
                .findByBarbeiroIdAndStatusNotInAndDataHoraInicioBetween(
                        barbeiroId, statusExcluidos, inicioDia, fimDia
                );

        List<HorarioDisponivelDTO> horariosDisponiveis = new ArrayList<>();

        LocalTime inicioExpediente = horarioTrabalho.getHoraInicio();
        LocalTime fimExpediente = horarioTrabalho.getHoraFim();

        int intervaloEntreSlots = 30;

        LocalTime horarioAtual = inicioExpediente;

        while (horarioAtual.plusMinutes(duracaoServico).isBefore(fimExpediente) ||
                horarioAtual.plusMinutes(duracaoServico).equals(fimExpediente)) {

            LocalDateTime slotInicio = data.atTime(horarioAtual);
            LocalDateTime slotFim = slotInicio.plusMinutes(duracaoServico);

            boolean conflito = false;
            for (Agendamento agendamento : agendamentosExistentes) {
                if (slotInicio.isBefore(agendamento.getDataHoraFim()) &&
                    slotFim.isAfter(agendamento.getDataHoraInicio())) {
                    conflito = true;
                    break;
                }
            }

            if(!conflito) {
                horariosDisponiveis.add(new HorarioDisponivelDTO(horarioAtual));
            }

            horarioAtual = horarioAtual.plusMinutes(intervaloEntreSlots);
        }
        return horariosDisponiveis;
    }

    /**
     * Converte a entidade {@link Agendamento} para o DTO de resposta.
     *
     * @param agendamento entidade persistida.
     * @return {@link RespostaAgendamentoDTO} contendo os dados a serem expostos pela API.
     */
    private RespostaAgendamentoDTO converterParaRespostaDTO(Agendamento agendamento) {
        return new RespostaAgendamentoDTO(
                agendamento.getId(),
                agendamento.getDataHoraInicio(),
                agendamento.getDataHoraFim(),
                agendamento.getStatus(),
                agendamento.getBarbeiro().getId(),
                agendamento.getBarbeiro().getNome(),
                agendamento.getCliente().getId(),
                agendamento.getCliente().getNome(),
                agendamento.getServico().getId(),
                agendamento.getServico().getNomeServico(),
                String.format("R$ %.2f", agendamento.getServico().getPreco())
        );
    }
}
