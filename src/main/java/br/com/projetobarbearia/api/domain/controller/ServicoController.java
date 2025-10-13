package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.AtualizarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.CriarServicoDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaServicoDTO;
import br.com.projetobarbearia.api.domain.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/barbeiros/{barbeiroId}/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @PostMapping
    public ResponseEntity<RespostaServicoDTO> criarServico(
            @PathVariable Long barbeiroId,
            @Valid @RequestBody CriarServicoDTO dto
            ) {
        RespostaServicoDTO servicoCriado = servicoService.criar(barbeiroId, dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{servicoId}")
                .buildAndExpand(barbeiroId, servicoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(servicoCriado);
    }

    @GetMapping
    public ResponseEntity<List<RespostaServicoDTO>> listarServicosDoBarbeiro(@PathVariable Long barbeiroId) {
        List<RespostaServicoDTO> servicos = servicoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{servicoId}")
    public ResponseEntity<RespostaServicoDTO> buscarServicoPorId(@PathVariable Long servicoId) {
        RespostaServicoDTO servico = servicoService.buscarPorId(servicoId);
        return ResponseEntity.ok(servico);
    }

    @PatchMapping("/{servicoId}")
    public ResponseEntity<RespostaServicoDTO> atualizarParcialmenteServico(@PathVariable Long servicoId, @Valid @RequestBody AtualizarServicoDTO dto) {
        RespostaServicoDTO servicoAtualizado = servicoService.atualizar(servicoId, dto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{servicoId}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long servicoId) {
        servicoService.deletar(servicoId);
        return ResponseEntity.noContent().build();
    }
}
