package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.CriarAgendamentoDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaAgendamentoDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.exception.RegraDeNegocioException;
import br.com.projetobarbearia.api.domain.model.Agendamento;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.model.Cliente;
import br.com.projetobarbearia.api.domain.model.Servico;
import br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus;
import br.com.projetobarbearia.api.domain.repository.AgendamentoRepository;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import br.com.projetobarbearia.api.domain.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Transactional
    public RespostaAgendamentoDTO agendar(CriarAgendamentoDTO dto) {

        Barbeiro barbeiro = barbeiroRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + dto.barbeiroId()));
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + dto.clienteId()));
        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + dto.servicoId()));

        LocalDateTime dataHoraInicio = dto.datahoraInicio();
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
