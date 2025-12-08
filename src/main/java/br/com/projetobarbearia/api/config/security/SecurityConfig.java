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
 * Configuração de segurança para a aplicação.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
