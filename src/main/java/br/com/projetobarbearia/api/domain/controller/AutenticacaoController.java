package br.com.projetobarbearia.api.domain.controller;

import br.com.projetobarbearia.api.domain.dto.DadosTokenJWT;
import br.com.projetobarbearia.api.domain.dto.LoginDTO;
import br.com.projetobarbearia.api.domain.infra.security.TokenService;
import br.com.projetobarbearia.api.domain.repository.BarbeiroRepository;
import br.com.projetobarbearia.api.domain.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável pela autenticação de clientes e barbeiros.
 *
 * <p>Expõe endpoints para login de cliente e barbeiro que validam credenciais
 * e retornam um token JWT encapsulado em {@link DadosTokenJWT} quando a
 * autenticação for bem-sucedida.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /auth/cliente/login — autentica um cliente.</li>
 *   <li>POST /auth/barbeiro/login — autentica um barbeiro.</li>
 * </ul>
 */
@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    /**
     * Repositório para operações de leitura/escrita de barbeiros.
     */
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    /**
     * Repositório para operações de leitura/escrita de clientes.
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Serviço de codificação/validação de senhas (BCrypt).
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Serviço responsável por gerar tokens JWT para usuários autenticados.
     */
    @Autowired
    private TokenService tokenService;

    /**
     * Realiza o login de um cliente.
     *
     * <p>Fluxo:
     * <ol>
     *   <li>Busca o cliente pelo email informado em {@link LoginDTO}.</li>
     *   <li>Verifica se a senha fornecida confere com a senha armazenada (hash) usando
     *       {@link PasswordEncoder#matches}.</li>
     *   <li>Gera um token JWT via {@link TokenService} e o retorna no corpo da resposta.</li>
     * </ol>
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: JSON contendo email e senha (mapeado para {@link LoginDTO}).</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e {@link DadosTokenJWT} no corpo.</li>
     *   <li>Erros: lança {@link RuntimeException} com a mensagem "Usuário ou senha inválidos"
     *       quando o email não é encontrado ou a senha não confere.</li>
     * </ul>
     *
     * @param dados credenciais do cliente a serem autenticadas.
     * @return ResponseEntity contendo o token JWT em {@link DadosTokenJWT} quando autenticado.
     * @throws RuntimeException quando credenciais inválidas.
     */
    @PostMapping("/cliente/login")
    public ResponseEntity<DadosTokenJWT> loginCliente(@RequestBody @Valid LoginDTO dados) {
        // 1. Buscar o cliente pelo email
        var cliente = clienteRepository.findByEmail(dados.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos")); // Boa prática: não dizer qual dos dois errou

        // 2. Verificar se a senha bate (Senha Pura vs Senha Hash)
        if (!passwordEncoder.matches(dados.senha(), cliente.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        // 3. Gerar o token
        var token = tokenService.gerarTokenCliente(cliente);

        return ResponseEntity.ok(new DadosTokenJWT(token));
    }

    /**
     * Realiza o login de um barbeiro.
     *
     * <p>Fluxo:
     * <ol>
     *   <li>Busca o barbeiro pelo email informado em {@link LoginDTO}.</li>
     *   <li>Valida a senha fornecida comparando com o hash armazenado.</li>
     *   <li>Gera e retorna um token JWT via {@link TokenService} em {@link DadosTokenJWT}.</li>
     * </ol>
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: JSON contendo email e senha (mapeado para {@link LoginDTO}).</li>
     *   <li>Saída: {@link ResponseEntity} com status 200 (OK) e {@link DadosTokenJWT} no corpo.</li>
     *   <li>Erros: lança {@link RuntimeException} com a mensagem "Usuário ou senha inválidos"
     *       quando o email não é encontrado ou a senha não confere.</li>
     * </ul>
     *
     * @param dados credenciais do barbeiro a serem autenticadas.
     * @return ResponseEntity contendo o token JWT em {@link DadosTokenJWT} quando autenticado.
     * @throws RuntimeException quando credenciais inválidas.
     */
    @PostMapping("/barbeiro/login")
    public ResponseEntity<DadosTokenJWT> loginBarbeiro(@RequestBody @Valid LoginDTO dados) {
        // 1. Buscar o barbeiro pelo email
        var barbeiro = barbeiroRepository.findByEmail(dados.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos")); // Boa prática: não dizer qual dos dois errou

        // 2. Verificar se a senha bate (Senha Pura vs Senha Hash)
        if (!passwordEncoder.matches(dados.senha(), barbeiro.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        // 3. Gerar o token
        var token = tokenService.gerarTokenBarbeiro(barbeiro);

        return ResponseEntity.ok(new DadosTokenJWT(token));
    }
}
