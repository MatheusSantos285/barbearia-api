package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.AtualizarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.CriarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaClienteDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.exception.RegraDeNegocioException;
import br.com.projetobarbearia.api.domain.model.Cliente;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço que encapsula a lógica de negócio relacionada a clientes.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Criar novos clientes com validação de unicidade (telefone e email).</li>
 *   <li>Buscar clientes por id ou telefone.</li>
 *   <li>Atualizar parcialmente dados do cliente.</li>
 *   <li>Listar e deletar clientes.</li>
 * </ul>
 *
 * <p>Exceções típicas lançadas:
 * <ul>
 *   <li>{@link RegraDeNegocioException} para violações de regras (por exemplo, telefone ou email já cadastrado).</li>
 *   <li>{@link EntidadeNaoEncontradaException} quando a entidade solicitada não existe.</li>
 * </ul>
 */
@Service
public class ClienteService {

    /**
     * Repositório utilizado para operações de persistência e consulta de clientes.
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Encoder de senhas (BCrypt) usado para armazenar senhas de forma segura.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo cliente aplicando validações de unicidade.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CriarClienteDTO} contendo nome, telefone, email e senha.</li>
     *   <li>Saída: {@link RespostaClienteDTO} com os dados do cliente criado.</li>
     *   <li>Erros: lança {@link RegraDeNegocioException} se telefone ou email já estiverem cadastrados.</li>
     * </ul>
     *
     * @param dto dados para criação do cliente.
     * @return DTO com os dados do cliente persistido.
     */
    @Transactional
    public RespostaClienteDTO criar(CriarClienteDTO dto) {
        Optional<Cliente> clienteExistente = clienteRepository.findByTelefone(dto.telefone());
        if(clienteExistente.isPresent()) {
            throw new RegraDeNegocioException("Já existe um cliente cadastrado com o telefone: " + dto.telefone());
        }

        boolean emailExists = clienteRepository.existsByEmail(dto.email());
        if (emailExists) {
            throw new RegraDeNegocioException("Já existe um cliente cadastrado com o email: " + dto.email());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setTelefone(dto.telefone());
        cliente.setEmail(dto.email());

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        cliente.setSenha(senhaCriptografada);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return converterParaRespostaDTO(clienteSalvo);
    }

    /**
     * Busca um cliente por seu identificador.
     *
     * @param id identificador do cliente.
     * @return {@link RespostaClienteDTO} com os dados do cliente encontrado.
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     */
    public RespostaClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + id));
        return converterParaRespostaDTO(cliente);
    }

    /**
     * Busca um cliente por telefone.
     *
     * @param telefone telefone do cliente.
     * @return {@link RespostaClienteDTO} com os dados do cliente encontrado.
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     */
    public RespostaClienteDTO buscarPorTelefone(String telefone) {
        Cliente cliente = clienteRepository.findByTelefone(telefone)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com telefone: " + telefone));
        return converterParaRespostaDTO(cliente);
    }

    /**
     * Atualiza parcialmente os dados de um cliente existente.
     *
     * <p>Campos suportados para atualização: nome e telefone. Valida se o telefone informado
     * já está em uso por outro cliente antes de aplicar a alteração.
     *
     * @param id identificador do cliente a ser atualizado.
     * @param dto DTO contendo os campos a serem atualizados.
     * @return {@link RespostaClienteDTO} com os dados atualizados do cliente.
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     * @throws RegraDeNegocioException se o novo telefone pertencer a outro cliente.
     */
    @Transactional
    public RespostaClienteDTO atualizarParcialmente(Long id, AtualizarClienteDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + id));

        if(dto.nome() != null && !dto.nome().isBlank()) {
            clienteExistente.setNome(dto.nome());
        }

        if(dto.telefone() != null && !dto.telefone().isBlank()) {
            Optional<Cliente> clienteComMesmoTelefone = clienteRepository.findByTelefone(dto.telefone());
            if(clienteComMesmoTelefone.isPresent() && !clienteComMesmoTelefone.get().getId().equals(id)) {
                throw new RegraDeNegocioException("Já existe um cliente cadastrado com o telefone: " + dto.telefone());
            }
            clienteExistente.setTelefone(dto.telefone());
        }

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return converterParaRespostaDTO(clienteAtualizado);
    }

    /**
     * Recupera todos os clientes cadastrados.
     *
     * @return lista de {@link RespostaClienteDTO} com os clientes existentes.
     */
    public List<RespostaClienteDTO> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deleta um cliente pelo seu identificador.
     *
     * <p>Observação: existe um TODO para impedir exclusão quando o cliente possuir agendamentos.
     *
     * @param id identificador do cliente a ser removido.
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     */
    @Transactional
    public void deletar(Long id) {
        // Verifica existência; lança EntidadeNaoEncontradaException em caso negativo
        clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + id));

        // TODO: Adicionar verificação futura - não deletar cliente se ele tiver agendamentos
        clienteRepository.deleteById(id);
    }

    /**
     * Converte a entidade {@link Cliente} para o DTO de resposta.
     *
     * @param cliente entidade a ser convertida.
     * @return {@link RespostaClienteDTO} com os campos expostos pela API.
     */
    private RespostaClienteDTO converterParaRespostaDTO(Cliente cliente) {
        return new RespostaClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
    }
}
