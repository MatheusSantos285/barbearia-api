package br.com.projetobarbearia.api.domain.infra.security;

import br.com.projetobarbearia.api.domain.model.Cliente;
import br.com.projetobarbearia.api.domain.model.Barbeiro;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Serviço responsável pela criação e validação de tokens JWT usados para autenticação.
 *
 * <p>Responsabilidades principais:
 * <ul>
 *   <li>Gerar tokens JWT para clientes e barbeiros contendo claims essenciais (id, role, subject).</li>
 *   <li>Validar tokens recebidos e extrair informações (subject e claims).</li>
 * </ul>
 *
 * <p>Este serviço usa a biblioteca Auth0 JWT para criar e verificar tokens com
 * algoritmo HMAC256. A chave secreta é injetada a partir da propriedade
 * {@code api.security.token.secret}.</p>
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT para um cliente.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: objeto {@link Cliente} com email e id não nulos.</li>
     *   <li>Saída: {@link String} contendo o token JWT assinado.</li>
     *   <li>Erros: encapsula {@link JWTCreationException} em {@link RuntimeException} em caso de falha ao gerar o token.</li>
     * </ul>
     *
     * @param cliente entidade do cliente para a qual o token será gerado (usa email e id).
     * @return token JWT assinado contendo issuer, subject, id, role e data de expiração.
     * @throws RuntimeException se ocorrer erro ao criar o token JWT.
     */
    public String gerarTokenCliente(Cliente cliente) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Barbearia") // Quem emitiu
                    .withSubject(cliente.getEmail()) // Quem é o dono do token (Email é único)
                    .withClaim("id", cliente.getId()) // Guardamos o ID dentro do token
                    .withClaim("role", "CLIENTE") // Guardamos o tipo de usuário
                    .withExpiresAt(dataExpiracao()) // Validade
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Gera um token JWT para um barbeiro.
     *
     * <p>Mesma responsabilidade que {@link #gerarTokenCliente(Cliente)} porém para a entidade {@link Barbeiro}.
     *
     * @param barbeiro entidade Barbeiro usada para popular subject e claims do token.
     * @return token JWT assinado contendo issuer, subject, id, role e data de expiração.
     * @throws RuntimeException se ocorrer erro ao criar o token JWT.
     */
    public String gerarTokenBarbeiro(Barbeiro barbeiro) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Barbearia")
                    .withSubject(barbeiro.getEmail())
                    .withClaim("id", barbeiro.getId())
                    .withClaim("role", "BARBEIRO")
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida o token JWT e retorna o subject (email) nele contido.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: token JWT como {@link String}.</li>
     *   <li>Saída: {@link String} representando o subject (email) do token.</li>
     *   <li>Erros: lança {@link RuntimeException} quando o token for inválido ou expirado.</li>
     * </ul>
     *
     * @param tokenJWT token JWT a ser verificado.
     * @return subject (email) presente no token validado.
     * @throws RuntimeException se o token for inválido ou expirado.
     */
    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Barbearia") // Verifica se foi a gente mesmo que emitiu
                    .build()
                    .verify(tokenJWT) // Se o token estiver expirado ou adulterado, lança exceção aqui
                    .getSubject(); // Retorna o email que estava guardado
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    /**
     * Lê uma claim textual do token JWT.
     *
     * <p>Utilizado, por exemplo, para recuperar o claim {@code role} salvo no token.
     *
     * @param tokenJWT token JWT a ser verificado.
     * @param claimName nome da claim a ser lida (por exemplo, {@code "role"}).
     * @return valor da claim como {@link String}.
     * @throws RuntimeException se o token for inválido ou expirado.
     */
    public String getClaim(String tokenJWT, String claimName) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Barbearia")
                    .build()
                    .verify(tokenJWT)
                    .getClaim(claimName) // Pega o valor da chave "role"
                    .asString();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    /**
     * Calcula a data de expiração do token (instante) considerando o fuso horário local.
     *
     * @return {@link Instant} representando a data/hora de expiração do token.
     */
    private Instant dataExpiracao() {
        // Token vale por 2 horas (GMT-3 para Brasil)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}