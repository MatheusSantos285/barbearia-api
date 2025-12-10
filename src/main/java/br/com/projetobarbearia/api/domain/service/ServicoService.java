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

/**
 * Serviço que gerencia operações de CRUD para os serviços oferecidos pelos barbeiros.
 *
 * <p>Responsabilidades principais:
 * <ul>
 *   <li>Criar serviços vinculados a um barbeiro.</li>
 *   <li>Listar serviços de um barbeiro.</li>
 *   <li>Buscar, atualizar parcialmente e deletar serviços existentes.</li>
 * </ul>
 *
 * <p>Exceções lançadas: {@link EntidadeNaoEncontradaException} quando um recurso referenciado
 * não é encontrado (por exemplo, barbeiro ou serviço).</p>
 */
@Service
public class ServicoService {

    /**
     * Repositório para persistência e busca de entidades {@link Servico}.
     */
    @Autowired
    private ServicoRepository servicoRepository;

    /**
     * Repositório para validação de existência do {@link Barbeiro} associado ao serviço.
     */
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    /**
     * Cria um novo serviço associado ao barbeiro informado.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarServicoDTO} contendo nome, duração e preço, além do {@code barbeiroId}.</li>
     *   <li>Saída: {@link RespostaServicoDTO} representando o serviço persistido.</li>
     *   <li>Erros: {@link EntidadeNaoEncontradaException} quando o barbeiro não existir.</li>
     * </ul>
     *
     * @param barbeiroId identificador do barbeiro dono do serviço.
     * @param dto dados do serviço a ser criado.
     * @return DTO com os dados do serviço criado.
     * @throws EntidadeNaoEncontradaException se o barbeiro não existir.
     */
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

    /**
     * Retorna a lista de serviços de um barbeiro.
     *
     * @param barbeiroId identificador do barbeiro cujos serviços serão retornados.
     * @return lista de {@link RespostaServicoDTO} pertencentes ao barbeiro.
     * @throws EntidadeNaoEncontradaException se o barbeiro não existir.
     */
    public List<RespostaServicoDTO> listarPorBarbeiro(Long barbeiroId) {
        barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<Servico> servicos = servicoRepository.findByBarbeiroId(barbeiroId);

        return servicos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um serviço pelo seu identificador.
     *
     * @param id identificador do serviço.
     * @return {@link RespostaServicoDTO} com os dados do serviço encontrado.
     * @throws EntidadeNaoEncontradaException se o serviço não existir.
     */
    public RespostaServicoDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + id));
        return converterParaRespostaDTO(servico);
    }

    /**
     * Atualiza parcialmente um serviço existente.
     *
     * <p>Campos suportados para atualização: nome do serviço, duração e preço.
     *
     * @param id identificador do serviço a ser atualizado.
     * @param dto DTO com os campos a serem atualizados.
     * @return {@link RespostaServicoDTO} com os dados atualizados do serviço.
     * @throws EntidadeNaoEncontradaException se o serviço não existir.
     */
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

    /**
     * Deleta um serviço pelo seu identificador.
     *
     * @param id identificador do serviço a ser removido.
     * @throws EntidadeNaoEncontradaException se o serviço não existir.
     */
    public void deletar(Long id) {
        servicoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço não encontrado com ID: " + id));

        servicoRepository.deleteById(id);
    }

    /**
     * Converte a entidade {@link Servico} para o DTO de resposta.
     *
     * @param servico entidade a ser convertida.
     * @return {@link RespostaServicoDTO} com os dados expostos pela API.
     */
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
