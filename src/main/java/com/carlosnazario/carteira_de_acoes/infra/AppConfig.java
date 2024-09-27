package com.carlosnazario.carteira_de_acoes.infra;

import com.carlosnazario.carteira_de_acoes.api.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

    /**
     * Cria uma instância do RestTemplate para realizar chamadas HTTP.
     * @return Uma nova instância de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Cria uma instância do AuthInterceptor para adicionar o cabeçalho de autorização às requisições HTTP.
     * @param apiKey A chave de API a ser usada para a autenticação.
     * @return Uma nova instância de AuthInterceptor.
     */
    @Bean
    public AuthInterceptor authInterceptor(@Value("${brapi.api-key}") String apiKey) {
        return new AuthInterceptor(apiKey);
    }
}
