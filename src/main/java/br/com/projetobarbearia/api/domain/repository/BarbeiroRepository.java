package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.Barbeiro;
import br.com.projetobarbearia.api.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
    Optional<Barbeiro> findByTelefone(String telefone);

    boolean existsByEmail(String email);

    Optional<Barbeiro> findByEmail(String email);
}
