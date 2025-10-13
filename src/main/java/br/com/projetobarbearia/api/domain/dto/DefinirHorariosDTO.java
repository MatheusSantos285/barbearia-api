package br.com.projetobarbearia.api.domain.dto;

import br.com.projetobarbearia.api.domain.model.HorarioTrabalho;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DefinirHorariosDTO(
        @NotEmpty
        @Valid
        List<HorarioTrabalho> horarios
) {}
