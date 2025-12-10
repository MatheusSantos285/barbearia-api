package br.com.projetobarbearia.api.config.security;

import br.com.projetobarbearia.api.domain.infra.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configura e aplica as regras de segurança HTTP da aplicação.
 *
 * <p>Principais pontos:
 * <ul>
 *   <li>Habilita CORS com configurações padrão.</li>
 *   <li>Desabilita CSRF, pois a aplicação utiliza autenticação stateless com JWT.</li>
 *   <li>Define a política de sessão como {@code STATELESS}.</li>
 *   <li>Especifica rotas públicas e protegidas, e as autorizações por papel (role).</li>
 *   <li>Registra o filtro personalizado de segurança antes do filtro padrão de autenticação.</li>
 * </ul>
 *
 * <p>Seguindo o padrão de documentação do Google (Javadoc),
 * os comentários descrevem o propósito, comportamento e contratos dos métodos.</n *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filtro responsável por validar o token JWT e popular o contexto de segurança
     * antes do processamento pelo {@link UsernamePasswordAuthenticationFilter}.
     */
    @Autowired
    private SecurityFilter securityFilter;

    /**
     * Cria e configura o {@link SecurityFilterChain} que define as regras de segurança
     * para todas as requisições HTTP da aplicação.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link HttpSecurity} fornecido pelo Spring.</li>
     *   <li>Saída: Instância imutável de {@link SecurityFilterChain} pronta para uso.</li>
     *   <li>Erros: Pode lançar {@link Exception} se ocorrer alguma falha na construção da cadeia.</li>
     * </ul>
     *
     * <p>Rotas públicas:
     * <ul>
     *   <li>POST /auth/** (login).</li>
     *   <li>POST /clientes (cadastro de cliente).</li>
     *   <li>GET /barbeiros (lista de barbeiros).</li>
     * </ul>
     *
     * <p>Autorização por perfis:
     * <ul>
     *   <li>Rotas /barbeiro/** requerem papel {@code BARBEIRO}.</li>
     *   <li>Rotas /cliente/** requerem papel {@code CLIENTE}.</li>
     * </ul>
     *
     * <p>Demais rotas exigem autenticação (token válido).</p>
     *
     * @param http objeto de configuração de segurança HTTP.
     * @return cadeia de filtros de segurança configurada.
     * @throws Exception se ocorrer erro ao construir a cadeia de filtros.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // ROTAS PÚBLICAS (Login e Cadastro não precisam de token, senão ninguém entra)
                    req.requestMatchers(HttpMethod.POST, "/auth/**").permitAll(); // Login
                    req.requestMatchers(HttpMethod.POST, "/clientes").permitAll(); // Cadastro de Cliente
                    req.requestMatchers(HttpMethod.GET, "/barbeiros").permitAll(); // Listar barbeiros na home
                    req.requestMatchers(HttpMethod.GET, "/barbeiros/*/servicos").permitAll(); // Listar serviços
                    req.requestMatchers("/barbeiro/**").hasRole("BARBEIRO");
                    req.requestMatchers("/cliente/**").hasRole("CLIENTE");

                    // ROTAS FECHADAS (Todas as outras exigem token)
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // <--- Colocamos nosso filtro antes do filtro padrão
                .build();
    }

    /**
     * Fornece um {@link PasswordEncoder} baseado em BCrypt para hashing de senhas.
     *
     * <p>BCrypt é recomendado por ser adaptativo e resistente a ataques de força bruta.
     *
     * @return encoder de senhas utilizando {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
