package br.com.projetobarbearia.api.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity // Marca esta classe como uma entidade JPA (uma tabela no banco)
@Table(name = "barbeiros") // Define o nome da tabela no banco de dados
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Gera equals e hashCode apenas com os campos anotados com @EqualsAndHashCode.Include
public class Barbeiro {
    @Id // Marca este campo como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do ID (auto-incremento)
    @EqualsAndHashCode.Include // Inclui este campo no equals e hashCode
    private Long id;

    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String nome;

    @Column(nullable = false, unique = true) // Define que esta coluna não pode ser nula e deve ser única
    private String email;

    @Column(name = "senha_hash", nullable = false) // Define que esta coluna não pode ser nula e mapeia para "senha_hash" no banco
    private String senha;

    @Column(nullable = false) // Define que esta coluna não pode ser nula
    private String telefone;
}
