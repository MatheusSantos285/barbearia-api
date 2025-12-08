# 💈 Barbearia Top - API Backend

API RESTful desenvolvida para gerenciamento de agendamentos de uma barbearia, controlando fluxo de clientes, disponibilidade de barbeiros e segurança.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT** (Autenticação e Autorização)
- **Spring Data JPA** (Persistência)
- **H2 Database / MySQL** (Banco de dados)
- **Flyway** (Migração de banco de dados)
- **Bean Validation** (Validação de dados)
- **Lombok**

## ⚙️ Funcionalidades

- **Autenticação:** Login e Cadastro com criptografia de senha (BCrypt) e Token JWT.
- **Perfis de Acesso:** Separação total entre rotas de `CLIENTE` e `BARBEIRO`.
- **Gestão de Serviços:** Barbeiros podem cadastrar e precificar seus serviços.
- **Gestão de Horários:** Definição de jornada de trabalho e bloqueio de horários.
- **Agendamento Inteligente:**
  - Verificação automática de conflito de horários.
  - Validação de dias de funcionamento.
  - Histórico de agendamentos por usuário.

## 🛠️ Como rodar o projeto

1. Clone o repositório.
2. Configure o banco de dados no `application.properties`.
3. Execute o projeto via IDE ou Maven:
   ```bash
   ./mvnw spring-boot:run
4. A API estará disponível em http://localhost:8080.

Desenvolvido por *Matheus Santos*.
