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

@Service
public class TokenService {

    // Em produção, isso viria do application.properties, mas para MVP pode ficar aqui
    // Dica de Sênior: Nunca comite segredos reais no Git.
    @Value("${api.security.token.secret}")
    private String secret;

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

    // Método sobrecarregado para Barbeiro
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

    // Método para validar e recuperar o "subject" (email) do token
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

    // Método para ler qualquer claim (informação extra) do token
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

    private Instant dataExpiracao() {
        // Token vale por 2 horas (GMT-3 para Brasil)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}