package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByBarbeiroId(Long barbeiroId);
}
