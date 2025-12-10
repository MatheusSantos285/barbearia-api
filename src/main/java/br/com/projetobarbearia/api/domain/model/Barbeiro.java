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
 * Entidade que representa um barbeiro do sistema.
 *
 * <p>Esta classe é mapeada para a tabela {@code barbeiros} e implementa a interface
 * {@link UserDetails} para integração com o Spring Security. Cada barbeiro possui
 * dados básicos de contato (nome, email, telefone) e uma senha armazenada como hash.
 *
 * <p>Observações:
 * <ul>
 *   <li>O campo {@code email} é utilizado como username para autenticação.</li>
 *   <li>O método {@link #getAuthorities()} fornece a role {@code ROLE_BARBEIRO}.</li>
 *   <li>O campo {@code senha} é anotado com {@link JsonIgnore} para não vazar em JSON.</li>
 * </ul>
 */
@Entity // Marca esta classe como uma entidade JPA (uma tabela no banco)
@Table(name = "barbeiros") // Define o nome da tabela no banco de dados
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Gera equals e hashCode apenas com os campos anotados com @EqualsAndHashCode.Include
public class Barbeiro implements UserDetails {

    /**
     * Identificador único do barbeiro.
     *
     * <p>Gerado automaticamente pelo banco (strategy = IDENTITY) e incluído em
     * equals/hashCode via {@link EqualsAndHashCode.Include}.
     */
    @Id // Marca este campo como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do ID (auto-incremento)
    @EqualsAndHashCode.Include // Inclui este campo no equals e hashCode
    private Long id;

    /**
     * Nome completo do barbeiro.
     *
     * <p>Campo obrigatório (nullable = false).
     */
    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String nome;

    /**
     * Email do barbeiro, usado como login (username) no sistema.
     *
     * <p>Campo obrigatório e único (nullable = false, unique = true).
     */
    @Column(nullable = false, unique = true) // Define que esta coluna não pode ser nula e deve ser única
    private String email;

    /**
     * Hash da senha do barbeiro.
     *
     * <p>Mapeado para a coluna {@code senha_hash} no banco e ignorado na serialização JSON
     * via {@link JsonIgnore} para evitar exposição.
     */
    @Column(name = "senha_hash", nullable = false) // Define que esta coluna não pode ser nula e mapeia para "senha_hash" no banco
    @JsonIgnore
    private String senha;

    /**
     * Telefone de contato do barbeiro.
     *
     * <p>Campo obrigatório (nullable = false).
     */
    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String telefone;

    /**
     * Retorna as autoridades (roles) associadas a este usuário para o Spring Security.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: nenhum parâmetro.</li>
     *   <li>Saída: coleção contendo {@code ROLE_BARBEIRO}.</li>
     * </ul>
     *
     * @return coleção de autoridades do usuário.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define o perfil desse usuário
        return List.of(new SimpleGrantedAuthority("ROLE_BARBEIRO"));
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
     * @return email do barbeiro.
     */
    @Override
    public String getUsername() {
        return email; // O nosso "login" é o email
    }

    /**
     * Indica se a conta do usuário não expirou.
     *
     * <p>A implementação atual sempre retorna {@code true} (conta válida).
     * Em cenários futuros, pode verificar data de expiração ou status.
     *
     * @return {@code true} se a conta não expirou.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indica se a conta do usuário não está bloqueada.
     *
     * <p>A implementação atual sempre retorna {@code true} (não bloqueado).
     *
     * @return {@code true} se a conta não estiver bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indica se as credenciais do usuário (senha) não expiraram.
     *
     * <p>A implementação atual sempre retorna {@code true}.
     *
     * @return {@code true} se as credenciais não expiraram.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indica se a conta do usuário está habilitada.
     *
     * <p>A implementação atual sempre retorna {@code true}.
     *
     * @return {@code true} se a conta estiver habilitada.
     */
    @Override
    public boolean isEnabled() { return true; }
}
