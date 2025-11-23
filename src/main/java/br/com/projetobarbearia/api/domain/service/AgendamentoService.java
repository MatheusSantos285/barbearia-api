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

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private BarbeiroRepository barbeiroRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private HorarioTrabalhoRepository  horarioTrabalhoRepository;

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

    public List<RespostaAgendamentoDTO> listarPorBarbeiro(Long barbeiroId) {
        barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<Agendamento> agendamentos = agendamentoRepository.findByBarbeiroId(barbeiroId);

        return agendamentos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    public List<RespostaAgendamentoDTO> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + clienteId));

        List<Agendamento> agendamentos = agendamentoRepository.findByClienteId(clienteId);

        return agendamentos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

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
