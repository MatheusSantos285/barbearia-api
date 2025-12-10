package br.com.projetobarbearia.api.domain.model;

import br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Entidade que representa um agendamento de serviço na barbearia.
 *
 * <p>Um agendamento vincula um {@link Cliente}, um {@link Barbeiro} e um {@link Servico}
 * a um intervalo de tempo ({@code dataHoraInicio} — {@code dataHoraFim}) com um
 * {@link AgendamentoStatus} que indica o estado atual (por exemplo, MARCADO, CANCELADO).
 *
 * <p>Persistida na tabela {@code agendamentos} com as colunas principais mapeadas
 * para data/hora, status e chaves estrangeiras para as entidades relacionadas.
 */
@Entity
@Table(name = "agendamentos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Agendamento {

    /**
     * Identificador único do agendamento.
     *
     * <p>Gerado automaticamente pelo banco de dados (strategy = IDENTITY).
     */
    @Id // Marca este campo como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do ID (auto-incremento)
    @EqualsAndHashCode.Include // Inclui este campo no equals e hashCode
    private Long id;

    /**
     * Data e hora de início do agendamento.
     *
     * <p>Campo obrigatório ({@code nullable = false}). Deve ser anterior a {@link #dataHoraFim}.
     */
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    /**
     * Data e hora de término do agendamento.
     *
     * <p>Campo obrigatório ({@code nullable = false}). Normalmente calculado a partir da
     * duração do {@link Servico} associado ao agendamento.
     */
    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    /**
     * Estado atual do agendamento.
     *
     * <p>Armazenado como {@link Enum#name()} na coluna do banco (EnumType.STRING).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgendamentoStatus status;

    /**
     * Barbeiro responsável pelo agendamento.
     *
     * <p>Relacionamento Many-to-One com a entidade {@link Barbeiro}. Chave estrangeira
     * mapeada na coluna {@code barbeiro_id} (não nula).
     */
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;

    /**
     * Cliente que realizou o agendamento.
     *
     * <p>Relacionamento Many-to-One com a entidade {@link Cliente}. Chave estrangeira
     * mapeada na coluna {@code cliente_id} (não nula).
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Serviço agendado.
     *
     * <p>Relacionamento Many-to-One com a entidade {@link Servico}. Chave estrangeira
     * mapeada na coluna {@code servico_id} (não nula). A duração do serviço costuma
     * ser usada para calcular {@link #dataHoraFim} ao criar o agendamento.
     */
    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;
}
