package br.com.projetobarbearia.api.domain.infra.security; // Ajuste o pacote se necessário

import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de segurança que processa o token JWT presente no header Authorization
 * de cada requisição e popula o contexto de segurança do Spring (SecurityContext).
 *
 * <p>Comportamento principal:
 * <ol>
 *   <li>Extrai o token JWT do cabeçalho Authorization (Bearer).</li>
 *   <li>Valida/extrai claims (por exemplo, subject/email e role) usando {@link TokenService}.</li>
 *   <li>Busca o {@link UserDetails} correspondente na base (cliente ou barbeiro) conforme a role.</li>
 *   <li>Povo a {@link SecurityContextHolder} com uma {@link UsernamePasswordAuthenticationToken}
 *       contendo o usuário autenticado e suas autorizações.</li>
 * </ol>
 *
 * <p>Esse filtro estende {@link OncePerRequestFilter} para garantir execução uma única vez por requisição.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    /**
     * Serviço responsável por operações com tokens JWT (validação e leitura de claims).
     */
    @Autowired
    private TokenService tokenService;

    /**
     * Repositório para recuperar usuários do tipo Cliente por email (subject do token).
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Repositório para recuperar usuários do tipo Barbeiro por email (subject do token).
     */
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    /**
     * Executa o filtro principal que autentica a requisição com base no token JWT.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link HttpServletRequest}, {@link HttpServletResponse} e {@link FilterChain}.</li>
     *   <li>Saída: nenhum valor retornado; como efeito colateral, o {@link SecurityContextHolder}
     *       pode ser populado com a autenticação do usuário quando o token for válido.</li>
     *   <li>Erros: lança {@link ServletException} ou {@link IOException} conforme a API do filtro.</li>
     * </ul>
     *
     * <p>Notas de implementação:
     * <ul>
     *   <li>Se o token não estiver presente, o filtro apenas delega a chamada sem alterar o contexto.</li>
     *   <li>Se o token estiver presente mas o usuário não for encontrado, nenhuma autenticação é adicionada;</li>
     *       isso permite que outros filtros ou tratadores decidam a resposta apropriada.
     * </ul>
     *
     * @param request  requisição HTTP atual.
     * @param response resposta HTTP atual.
     * @param filterChain cadeia de filtros para delegar o processamento.
     * @throws ServletException quando ocorre erro no processamento do servlet.
     * @throws IOException quando ocorre erro de I/O durante o processamento.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

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

    /**
     * Recupera o token JWT do header Authorization da requisição.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link HttpServletRequest} contendo possivelmente o header Authorization no formato "Bearer &lt;token&gt;".</li>
     *   <li>Saída: {@link String} com o token puro (sem o prefixo "Bearer ") ou {@code null} se o header não existir.</li>
     * </ul>
     *
     * @param request requisição HTTP da qual o header Authorization será lido.
     * @return token JWT extraído do header, ou {@code null} se não presente.
     */
    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}