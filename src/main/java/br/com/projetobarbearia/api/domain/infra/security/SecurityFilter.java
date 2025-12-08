package br.com.projetobarbearia.api.domain.infra.security; // Ajuste o pacote se necessário

import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import br.com.projetobarbearia.api.domain.infra.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // 1. Validar e pegar o email
            var subject = tokenService.getSubject(tokenJWT);
            // 2. Pegar o tipo de usuário (ROLE) que salvamos ao gerar o token
            var role = tokenService.getClaim(tokenJWT, "role");

            UserDetails usuario = null;

            // 3. Decisão inteligente de qual tabela buscar
            if ("CLIENTE".equals(role)) {
                usuario = clienteRepository.findByEmail(subject).orElse(null);
            } else if ("BARBEIRO".equals(role)) {
                usuario = barbeiroRepository.findByEmail(subject).orElse(null);
            }

            // 4. Autenticação Padrão do Spring
            if (usuario != null) {
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}