package br.com.projetobarbearia.api.domain.repository;

import br.com.projetobarbearia.api.domain.model.Agendamento;
import br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de acesso aos agendamentos.
 * Extende {@link JpaRepository} fornecendo operações CRUD e consultas derivadas.
 */
@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    /**
     * Busca agendamentos de um barbeiro em um intervalo de data/hora, excluindo
     * determinados status.
     *
     * O nome do método é construído para que o Spring Data gere a query automaticamente:
     * - {@code findByBarbeiroId} indica que a comparação será feita pelo identificador da entidade relacionada {@code barbeiro}.
     * - {@code AndStatusNotIn} exclui uma lista de status.
     * - {@code AndDataHoraInicioBetween} filtra pelo intervalo de início.
     *
     * @param barbeiroId     id do barbeiro
     * @param statusExcluidos lista de status a serem excluídos
     * @param inicioDoDia    início do intervalo (inclusive)
     * @param fimDoDia       fim do intervalo (inclusive)
     * @return lista de agendamentos que atendem aos critérios
     */
    List<Agendamento> findByBarbeiroIdAndStatusNotInAndDataHoraInicioBetween(
        Long barbeiroId,
        List<AgendamentoStatus> statusExcluidos,
        LocalDateTime inicioDoDia,
        LocalDateTime fimDoDia
    );

    /**
     * Busca agendamentos pelo identificador do cliente.
     *
     * Importante: usar {@code ClienteId} no nome do método faz com que o Spring Data
     * compare o valor do parâmetro com a propriedade {@code cliente.id} da entidade,
     * permitindo passar um {@link Long} como parâmetro. Se o método fosse {@code findByCliente},
     * o Spring esperaria um objeto {@code Cliente} como parâmetro.
     *
     * @param clienteId id do cliente
     * @return lista de agendamentos do cliente informado
     */
    List<Agendamento> findByClienteId(Long clienteId);

    /**
     * Busca por agendamentos que conflitam com um novo horário para um barbeiro específico.
     * Um conflito existe se:
     * - O horário de início do novo agendamento está entre o início e o fim de um agendamento existente.
     * - O horário de fim do novo agendamento está entre o início e o fim de um agendamento existente.
     * - O novo agendamento "envolve" completamente um agendamento existente.
     * E que não estejam cancelados.
     */
    @Query("SELECT a FROM Agendamento a " +
            "WHERE a.barbeiro.id = :barbeiroId " +
            "AND a.status NOT IN (br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus.CANCELADO_PELO_CLIENTE, br.com.projetobarbearia.api.domain.model.enums.AgendamentoStatus.CANCELADO_PELO_BARBEIRO) " +
            "AND (" +
            "    (a.dataHoraInicio < :novoFim AND a.dataHoraFim > :novoInicio)" +
            ")")
    Optional<Agendamento> findConflictingAgendamento(
            Long barbeiroId,
            LocalDateTime novoInicio,
            LocalDateTime novoFim
    );
}
