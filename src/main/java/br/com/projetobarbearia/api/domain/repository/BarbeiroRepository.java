package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
}
