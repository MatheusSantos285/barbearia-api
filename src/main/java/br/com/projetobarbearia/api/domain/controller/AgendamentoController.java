package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.CriarAgendamentoDTO;
import br.com.projetobarbearia.api.domain.dto.HorarioDisponivelDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaAgendamentoDTO;
import br.com.projetobarbearia.api.domain.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<RespostaAgendamentoDTO> agendar(@Valid @RequestBody CriarAgendamentoDTO dto) {
        RespostaAgendamentoDTO  agendamentoCriado = agendamentoService.agendar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agendamentoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(agendamentoCriado);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<RespostaAgendamentoDTO> cancelarAgendamento(@PathVariable Long id) {
        RespostaAgendamentoDTO agendamentoCancelado = agendamentoService.cancelar(id);
        return ResponseEntity.ok(agendamentoCancelado);
    }

    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<RespostaAgendamentoDTO>> listarAgendamentosDoBarbeiro(@PathVariable Long barbeiroId) {
        List<RespostaAgendamentoDTO> agendamentos = agendamentoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok().body(agendamentos);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<RespostaAgendamentoDTO>> listarAgendamentosDoCliente(@PathVariable Long clienteId) {
        List<RespostaAgendamentoDTO> agendamentos = agendamentoService.listarPorCliente(clienteId);
        return ResponseEntity.ok().body(agendamentos);
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam Long servicoId,
            @RequestParam LocalDate data
    ) {
        List<HorarioDisponivelDTO> horarios = agendamentoService.listarHorariosDisponiveis(barbeiroId, servicoId, data);
        return ResponseEntity.ok(horarios);
    }
}
