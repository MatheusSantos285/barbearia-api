package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTelefone(String telefone);

    boolean existsByEmail(String email);

    Optional<Cliente> findByEmail(String email);
}
