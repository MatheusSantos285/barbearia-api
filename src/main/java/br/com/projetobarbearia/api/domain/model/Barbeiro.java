package br.com.projetobarbearia.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity // Marca esta classe como uma entidade JPA (uma tabela no banco)
@Table(name = "barbeiros") // Define o nome da tabela no banco de dados
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Gera equals e hashCode apenas com os campos anotados com @EqualsAndHashCode.Include
public class Barbeiro implements UserDetails {
    @Id // Marca este campo como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do ID (auto-incremento)
    @EqualsAndHashCode.Include // Inclui este campo no equals e hashCode
    private Long id;

    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String nome;

    @Column(nullable = false, unique = true) // Define que esta coluna não pode ser nula e deve ser única
    private String email;

    @Column(name = "senha_hash", nullable = false) // Define que esta coluna não pode ser nula e mapeia para "senha_hash" no banco
    @JsonIgnore
    private String senha;

    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String telefone;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define o perfil desse usuário
        return List.of(new SimpleGrantedAuthority("ROLE_BARBEIRO"));
    }

    @Override
    public String getPassword() {
        return senha; // Retorna a senha hash
    }

    @Override
    public String getUsername() {
        return email; // O nosso "login" é o email
    }

    // Configurações padrão (pode deixar tudo true para simplificar)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
