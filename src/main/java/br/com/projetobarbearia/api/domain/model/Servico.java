package br.com.projetobarbearia.api.domain.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Entidade que representa um serviço oferecido por um barbeiro.
 *
 * <p>Um {@code Servico} contém informação sobre o nome do serviço, sua duração
 * em minutos, preço e o {@link Barbeiro} responsável pela prestação do serviço.
 * A entidade é mapeada para a tabela {@code servicos} e é usada tanto para
 * apresentação quanto para cálculo de disponibilidade ao agendar.
 */
@Entity
@Table(name = "servicos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servico {

    /**
     * Identificador único do serviço.
     *
     * <p>Gerado automaticamente pelo banco (IDENTITY) e incluído em equals/hashCode.
     */
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome descritivo do serviço (ex.: Corte Masculino, Barba, etc.).
     *
     * <p>Mapeado para a coluna {@code nome_servico} e não pode ser nulo.
     */
    @Column(name = "nome_servico", nullable = false)
    private String nomeServico;

    /**
     * Duração do serviço em minutos.
     *
     * <p>Usada para calcular o horário de término do agendamento
     * ao agendar um serviço e para montar os slots de disponibilidade.
     */
    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    /**
     * Preço do serviço.
     *
     * <p>Representado por {@link BigDecimal} para precisão monetária e não pode ser nulo.
     */
    @Column(nullable = false)
    private BigDecimal preco;

    /**
     * Barbeiro responsável por este serviço.
     *
     * <p>Relacionamento Many-to-One com {@link Barbeiro}; a coluna estrangeira
     * {@code barbeiro_id} referencia o barbeiro dono do serviço.
     */
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;
}
