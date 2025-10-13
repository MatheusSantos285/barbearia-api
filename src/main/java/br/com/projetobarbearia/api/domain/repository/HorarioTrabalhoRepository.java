package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.HorarioTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> {

    List<HorarioTrabalho> findByBarbeiroId(Long barbeiroId);
}
