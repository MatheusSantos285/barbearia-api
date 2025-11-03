package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.AtualizarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.CriarClienteDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaClienteDTO;
import br.com.projetobarbearia.api.domain.model.Cliente;
import br.com.projetobarbearia.api.domain.service.ClienteService;
import jakarta.servlet.Servlet;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<RespostaClienteDTO> criarCliente(@Valid @RequestBody CriarClienteDTO dto) {
        RespostaClienteDTO clienteCriado = clienteService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(clienteCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RespostaClienteDTO> atualizarParcialmenteCliente(@PathVariable Long id, @Valid @RequestBody AtualizarClienteDTO dto) {
        RespostaClienteDTO clienteAtualizado = clienteService.atualizarParcialmente(id, dto);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @GetMapping
    public ResponseEntity<List<RespostaClienteDTO>> listarTodosClientes() {
        List<RespostaClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaClienteDTO> buscarClientePorId(@PathVariable Long id) {
        RespostaClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/telefone/{telefone}")
    public ResponseEntity<RespostaClienteDTO> buscarClientePorTelefone(@PathVariable String telefone) {
        RespostaClienteDTO cliente = clienteService.buscarPorTelefone(telefone);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
