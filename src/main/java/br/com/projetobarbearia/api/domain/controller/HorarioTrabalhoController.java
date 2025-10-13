package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.DefinirHorariosDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaHorarioTrabalhoDTO;
import br.com.projetobarbearia.api.domain.service.HorarioTrabalhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barbeiros/{barbeiroId}/horarios-trabalho")
public class HorarioTrabalhoController {

    @Autowired
    private HorarioTrabalhoService horarioTrabalhoService;

    @PutMapping
    public ResponseEntity<List<RespostaHorarioTrabalhoDTO>> definirHorarios(
            @PathVariable Long barbeiroId,
            @Valid @RequestBody DefinirHorariosDTO dto
            ) {
        List<RespostaHorarioTrabalhoDTO> horariosSalvos = horarioTrabalhoService.definirHorarios(barbeiroId, dto);
        return ResponseEntity.ok(horariosSalvos);
    }

    @GetMapping
    public ResponseEntity<List<RespostaHorarioTrabalhoDTO>> listarHorarios(@PathVariable Long barbeiroId) {
        List<RespostaHorarioTrabalhoDTO> horarios = horarioTrabalhoService.listarPorBarbeiro(barbeiroId);
        return ResponseEntity.ok(horarios);
    }
}
