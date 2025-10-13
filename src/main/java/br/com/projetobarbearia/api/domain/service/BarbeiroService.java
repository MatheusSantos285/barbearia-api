package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.AtualizarBarbeiroDTO;
import br.com.projetobarbearia.api.domain.dto.CriarBarbeiroDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaBarbeiroDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar as operações de Barbeiro.
 */
@Service
public class BarbeiroService {

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo barbeiro no sistema.
     *
     * @param dto DTO com os dados para a criação do barbeiro.
     * @return DTO com os dados do barbeiro criado.
     */
    public RespostaBarbeiroDTO criar(CriarBarbeiroDTO dto) {
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setNome(dto.nome());
        barbeiro.setEmail(dto.email());

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        barbeiro.setSenha(senhaCriptografada);
        barbeiro.setTelefone(dto.telefone());

        Barbeiro barbeiroRetorno = barbeiroRepository.save(barbeiro);

        return converterParaRespostaDTO(barbeiroRetorno);
    }

    /**
     * Busca um barbeiro pelo seu ID.
     *
     * @param id O ID do barbeiro a ser buscado.
     * @return DTO com os dados do barbeiro encontrado.
     * @throws EntidadeNaoEncontradaException se o barbeiro não for encontrado.
     */
    public RespostaBarbeiroDTO buscarPorId(Long id) {
        Barbeiro barbeiro = barbeiroRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + id));
        return converterParaRespostaDTO(barbeiro);
    }

    /**
     * Atualiza os dados de um barbeiro existente.
     *
     * @param id O ID do barbeiro a ser atualizado.
     * @param dto DTO com os dados a serem atualizados.
     * @return DTO com os dados do barbeiro atualizado.
     * @throws EntidadeNaoEncontradaException se o barbeiro não for encontrado.
     */
    public RespostaBarbeiroDTO atualizar(Long id, AtualizarBarbeiroDTO dto) {
        Barbeiro barbeiroExistente = barbeiroRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + id));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            barbeiroExistente.setNome(dto.nome());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            barbeiroExistente.setEmail(dto.email());
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(dto.senha());
            barbeiroExistente.setSenha(senhaCriptografada);
        }

        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            barbeiroExistente.setTelefone(dto.telefone());
        }

        Barbeiro barbeiroAtualizado = barbeiroRepository.save(barbeiroExistente);

        return converterParaRespostaDTO(barbeiroAtualizado);
    }

    /**
     * Lista todos os barbeiros cadastrados.
     *
     * @return Uma lista de DTOs com os dados de todos os barbeiros.
     */
    public List<RespostaBarbeiroDTO> listarTodos() {
        List<Barbeiro> barbeiros = barbeiroRepository.findAll();

        return barbeiros.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deleta um barbeiro pelo seu ID.
     *
     * @param id O ID do barbeiro a ser deletado.
     * @throws EntidadeNaoEncontradaException se o barbeiro não for encontrado.
     */
    public void deletar(Long id) {
        Barbeiro barbeiro = barbeiroRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com o ID: " + id));

        barbeiroRepository.deleteById(id);
    }

    /**
     * Converte uma entidade Barbeiro para um DTO de resposta.
     *
     * @param barbeiro A entidade Barbeiro a ser convertida.
     * @return O DTO de resposta com os dados do barbeiro.
     */
    private RespostaBarbeiroDTO converterParaRespostaDTO(Barbeiro barbeiro) {
        return new RespostaBarbeiroDTO(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEmail(),
                barbeiro.getTelefone()
        );
    }
}
