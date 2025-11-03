package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.AtualizarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.CriarBarbeiroDTO;
import br.com.projetobarbearia.api.domain.dto.CriarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaClienteDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.exception.RegraDeNegocioException;
import br.com.projetobarbearia.api.domain.model.Cliente;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public RespostaClienteDTO criar(CriarClienteDTO dto) {
        Optional<Cliente> clienteExistente = clienteRepository.findByTelefone(dto.telefone());
        if(clienteExistente.isPresent()) {
            throw new RegraDeNegocioException("Já existe um cliente cadastrado com o telefone: " + dto.telefone());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setTelefone(dto.telefone());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return converterParaRespostaDTO(clienteSalvo);
    }

    public RespostaClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + id));
        return converterParaRespostaDTO(cliente);
    }

    public RespostaClienteDTO buscarPorTelefone(String telefone) {
        Cliente cliente = clienteRepository.findByTelefone(telefone)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com telefone: " + telefone));
        return converterParaRespostaDTO(cliente);
    }

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

    public List<RespostaClienteDTO> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado com ID: " + id));

        // TODO: Adicionar verificação futura - não deletar cliente se ele tiver agendamentos
        clienteRepository.deleteById(id);
    }

    private RespostaClienteDTO converterParaRespostaDTO(Cliente cliente) {
        return new RespostaClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone()
        );
    }
}
