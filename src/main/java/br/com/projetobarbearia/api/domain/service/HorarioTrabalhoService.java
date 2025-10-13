package br.com.projetobarbearia.api.domain.service;

import br.com.projetobarbearia.api.domain.dto.DefinirHorariosDTO;
import br.com.projetobarbearia.api.domain.dto.RespostaHorarioTrabalhoDTO;
import br.com.projetobarbearia.api.domain.exception.EntidadeNaoEncontradaException;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.model.HorarioTrabalho;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.HorarioTrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioTrabalhoService {

    @Autowired
    private HorarioTrabalhoRepository horarioTrabalhoRepository;

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @Transactional
    public List<RespostaHorarioTrabalhoDTO> definirHorarios(Long barbeiroId, DefinirHorariosDTO dto) {
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId));

        List<HorarioTrabalho> horariosAntigos = horarioTrabalhoRepository.findByBarbeiroId(barbeiroId);
        horarioTrabalhoRepository.deleteAll(horariosAntigos);

        List<HorarioTrabalho> novosHorarios = dto.horarios().stream().map(horarioDia -> {
            HorarioTrabalho novoHorario = new HorarioTrabalho();
            novoHorario.setBarbeiro(barbeiro);
            novoHorario.setDiaSemana(horarioDia.getDiaSemana());
            novoHorario.setHoraInicio(horarioDia.getHoraInicio());
            novoHorario.setHoraFim(horarioDia.getHoraFim());
            return novoHorario;
        }).collect(Collectors.toList());
        
        List<HorarioTrabalho> horariosSalvos = horarioTrabalhoRepository.saveAll(novosHorarios);
        
        return horariosSalvos.stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    public List<RespostaHorarioTrabalhoDTO> listarPorBarbeiro(Long barbeiroId) {
        if (!barbeiroRepository.existsById(barbeiroId)) {
            throw new EntidadeNaoEncontradaException("Barbeiro não encontrado com ID: " + barbeiroId);
        }

        return horarioTrabalhoRepository.findByBarbeiroId(barbeiroId).stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    private RespostaHorarioTrabalhoDTO converterParaRespostaDTO(HorarioTrabalho horarioTrabalho) {
        return new RespostaHorarioTrabalhoDTO(
                horarioTrabalho.getId(),
                horarioTrabalho.getDiaSemana(),
                horarioTrabalho.getHoraInicio(),
                horarioTrabalho.getHoraFim(),
                horarioTrabalho.getBarbeiro().getId()
        );
    }

}
