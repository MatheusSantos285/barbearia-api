package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.AtualizarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.CriarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaServicoDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.model.Servico;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @Transactional
    public RespostaServicoDTO criar(Long barbeiroId, CriarServicoDTO dto) {
        // 1. Busca o barbeiro "dono" do serviço. Se não existir, lança exceção.
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        // 2. Cria o serviço e associa ao barbeiro.
        Servico servico = new Servico();
        servico.setNomeServico(dto.nomeServico());
        servico.setDuracaoMinutos(dto.duracaoMinutos());
        servico.setPreco(dto.preco());
        servico.setBarbeiro(barbeiro);

        // 3. Salva o serviço no banco de dados.
        Servico servicoSalvo = servicoRepository.save(servico);

        // 4. Converte para DTO de resposta e retorna.
        return converterParaRespostaDTO(servicoSalvo);
    }

    public List<RespostaServicoDTO> listarPorBarbeiro(Long barbeiroId) {
        barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<Servico> servicos = servicoRepository.findByBarbeiroId(barbeiroId);

        return servicos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    public RespostaServicoDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + id));
        return converterParaRespostaDTO(servico);
    }

    public RespostaServicoDTO atualizar(Long id, AtualizarServicoDTO dto) {
        Servico servicoExistente = servicoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + id));

        if (dto.nomeServico() != null && !dto.nomeServico().isBlank()) {
            servicoExistente.setNomeServico(dto.nomeServico());
        }

        if (dto.duracaoMinutos() != null && dto.duracaoMinutos() > 0) {
            servicoExistente.setDuracaoMinutos(dto.duracaoMinutos());
        }

        if (dto.preco() != null && dto.preco().compareTo(servicoExistente.getPreco()) != 0) {
            servicoExistente.setPreco(dto.preco());
        }

        Servico servicoAtualizado = servicoRepository.save(servicoExistente);
        return converterParaRespostaDTO(servicoAtualizado);
    }

    public void deletar(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + id));

        servicoRepository.deleteById(id);
    }

    private RespostaServicoDTO converterParaRespostaDTO(Servico servico) {
        return new RespostaServicoDTO(
                servico.getId(),
                servico.getNomeServico(),
                servico.getDuracaoMinutos(),
                servico.getPreco(),
                servico.getBarbeiro().getId()
        );
    }
}
