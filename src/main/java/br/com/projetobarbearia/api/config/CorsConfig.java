package br.com.projetobarbearia.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configura as regras de CORS (Cross-Origin Resource Sharing) para a aplicação.
 *
 * <p>Permite que clientes web executando em origens específicas (por exemplo, dev front-ends
 * em localhost) realizem requisições à API com métodos, cabeçalhos e credenciais autorizadas.
 *
 * <p>Principais comportamentos:
 * <ul>
 *   <li>Libera todos os caminhos ("/**").</li>
 *   <li>Permite origens: {@code http://localhost:5173} e {@code http://localhost:3000}.</li>
 *   <li>Permite métodos: GET, POST, PUT, DELETE, PATCH e OPTIONS.</li>
 *   <li>Permite todos os cabeçalhos.</li>
 *   <li>Habilita envio de credenciais (cookies/authorization headers).</li>
 * </ul>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Registra os mapeamentos de CORS da aplicação.
     *
     * <p>Contrato:
     * <ul>
     *   <li>Entrada: {@link CorsRegistry} fornecido pelo Spring MVC.</li>
     *   <li>Saída: Configuração aplicada ao registro; não retorna valor.</li>
     *   <li>Erros: Não há exceções declaradas; erros podem ocorrer em tempo de execução
     *       se houver conflito de configuração.</li>
     * </ul>
     *
     * <p>Notas:
     * <ul>
     *   <li>Ao usar {@code allowCredentials(true)}, especificar origens explícitas é recomendado
     *       (evitar {@code *}) para cumprir as regras do CORS.</li>
     *   <li>Em produção, ajuste as origens permitidas conforme o domínio do front-end real.</li>
     * </ul>
     *
     * @param registry registro de CORS no qual as regras serão adicionadas.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH",  "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
