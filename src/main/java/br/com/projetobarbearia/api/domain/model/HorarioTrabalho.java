package br.com.projetobarbearia.api.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalTime;

/**
 * Entidade que representa um horário de trabalho (turno) configurado para um barbeiro.
 *
 * <p>Um {@code HorarioTrabalho} define em que dia da semana e em que intervalo de horas
 * um barbeiro está disponível para atender clientes. A entidade é utilizada para
 * calcular disponibilidade e para mostrar o expediente semanal de cada barbeiro.
 *
 * <p>Mapeada para a tabela {@code horarios_trabalho} com os campos principais:
 * <ul>
 *   <li>{@code diaSemana} — dia da semana (0..6 ou 1..7 conforme sua convenção; verificar uso no serviço).</li>
 *   <li>{@code horaInicio} — horário de início do expediente.</li>
 *   <li>{@code horaFim} — horário de término do expediente.</li>
 *   <li>{@code barbeiro} — referência ao {@link Barbeiro} dono deste horário.</li>
 * </ul>
 */
@Entity
@Table(name = "horarios_trabalho")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HorarioTrabalho {

    /**
     * Identificador único do registro de horário.
     *
     * <p>Gerado automaticamente pelo banco de dados (IDENTITY).
     */
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Dia da semana ao qual este horário se aplica.
     *
     * <p>Formato/escala: inteiro. O serviço que consome este campo deve documentar
     * se usa 0..6 (domingo..sábado) ou 1..7 (segunda..domingo). Campo obrigatório.
     */
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    /**
     * Hora de início do expediente neste dia.
     *
     * <p>Ex.: {@code 09:00}. Campo obrigatório.
     */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de término do expediente neste dia.
     *
     * <p>Ex.: {@code 18:00}. Campo obrigatório; normalmente deve ser posterior a {@link #horaInicio}.
     */
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    /**
     * Barbeiro proprietário deste horário de trabalho.
     *
     * <p>Relacionamento Many-to-One com {@link Barbeiro}; campo não nulo.
     */
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;
}
