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

/**
 * Entidade que representa um cliente da barbearia.
 *
 * <p>Esta classe é mapeada para a tabela {@code clientes} e implementa {@link UserDetails}
 * para integração com o Spring Security. Um cliente possui informações de contato
 * (nome, telefone, email) e uma senha armazenada como hash.
 *
 * <p>Notas importantes:
 * <ul>
 *   <li>O campo {@code email} é utilizado como username para autenticação.</li>
 *   <li>O campo {@code senha} é anotado com {@link JsonIgnore} para não ser serializado em respostas JSON.</li>
 *   <li>O método {@link #getAuthorities()} fornece a role {@code ROLE_CLIENTE}.</li>
 * </ul>
 */
@Entity // Marca esta classe como uma entidade JPA (uma tabela no banco)
@Table(name = "clientes") // Define o nome da tabela no banco de dados
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente implements UserDetails {

    /**
     * Identificador único do cliente.
     *
     * <p>Gerado automaticamente pelo banco (strategy = IDENTITY). Serve como chave primária.
     */
    @Id // Marca este campo como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do ID (auto-incremento)
    @EqualsAndHashCode.Include // Inclui este campo no equals e hashCode
    private Long id;

    /**
     * Nome completo do cliente.
     *
     * <p>Campo obrigatório (nullable = false).
     */
    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String nome;

    /**
     * Telefone do cliente. Usado em alguns fluxos como identificador alternativo.
     *
     * <p>Campo obrigatório e único (nullable = false, unique = true).
     */
    @Column(nullable = false, unique = true) // Define que esta coluna não pode ser nula
    private String telefone;

    /**
     * Email do cliente, usado como login (username) no sistema.
     *
     * <p>Campo obrigatório e único (nullable = false, unique = true).
     */
    @Column(nullable = false, unique = true) // Define que esta coluna não pode ser nula
    private String email;

    /**
     * Hash da senha do cliente.
     *
     * <p>Mapeado para a coluna {@code senha_hash} no banco e ignorado na serialização JSON
     * via {@link JsonIgnore} para evitar exposição acidental.
     */
    @Column(name = "senha_hash", nullable = true)
    @JsonIgnore
    private String senha;

    /**
     * Retorna as autoridades (roles) associadas a este usuário para o Spring Security.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: nenhum parâmetro.</li>
     *   <li>Saída: coleção contendo {@code ROLE_CLIENTE}.</li>
     * </ul>
     *
     * @return coleção de autoridades do usuário.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define o perfil desse usuário
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    /**
     * Retorna a senha (hash) do usuário para uso pelo provedor de autenticação.
     *
     * @return senha (hash) armazenada.
     */
    @Override
    public String getPassword() {
        return senha; // Retorna a senha hash
    }

    /**
     * Retorna o identificador usado como username para login — aqui usamos o email.
     *
     * @return email do cliente.
     */
    @Override
    public String getUsername() {
        return email; // O nosso "login" é o email
    }

    // Configurações padrão de conta (implementação por enquanto sempre válida)

    /**
     * Indica se a conta do usuário não expirou.
     *
     * @return {@code true} se a conta não expirou.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indica se a conta do usuário não está bloqueada.
     *
     * @return {@code true} se a conta não estiver bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indica se as credenciais do usuário (senha) não expiraram.
     *
     * @return {@code true} se as credenciais não expiraram.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indica se a conta do usuário está habilitada.
     *
     * @return {@code true} se a conta estiver habilitada.
     */
    @Override
    public boolean isEnabled() { return true; }
}
