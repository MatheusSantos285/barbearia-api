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

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private BarbeiroRepository barbeiroRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

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
